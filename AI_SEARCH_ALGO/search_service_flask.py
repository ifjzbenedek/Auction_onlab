"""
Search Service - Qdrant alapú vektorkereső szolgáltatás
Ez a szolgáltatás a Qdrant vektordatabázissal kommunikál
és az embedding service-t használja a szövegek vektorizálásához.
"""

from flask import Flask, jsonify, request
from qdrant_client import QdrantClient
from qdrant_client.http import models
import requests
import logging
import uuid
import os
import google.generativeai as genai
from typing import List, Dict, Any, Optional

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)

QDRANT_HOST = "localhost"
QDRANT_PORT = 6333
EMBEDDING_SERVICE_URL = "http://localhost:5001"
COLLECTION_NAME = "auction_products"
VECTOR_SIZE = 384  # all-MiniLM-L6-v2 model vector size

genai.configure(api_key=os.getenv("GOOGLE_API_KEY"))
gemini_model = genai.GenerativeModel('gemini-2.0-flash')

qdrant_client = QdrantClient(host=QDRANT_HOST, port=QDRANT_PORT)

def ensure_collection_exists():
    """Create collection if it doesn't exist"""
    try:
        collections = qdrant_client.get_collections()
        collection_names = [col.name for col in collections.collections]
        
        if COLLECTION_NAME not in collection_names:
            logger.info(f"Creating collection: {COLLECTION_NAME}")
            qdrant_client.create_collection(
                collection_name=COLLECTION_NAME,
                vectors_config=models.VectorParams(
                    size=VECTOR_SIZE,
                    distance=models.Distance.COSINE
                )
            )
            logger.info("Collection successfully created")
        else:
            logger.info("Collection already exists")
            
    except Exception as e:
        logger.error(f"Error checking collection: {e}")
        raise

def extract_keywords_with_gemini(title: str, description: str) -> List[str]:
    """Extract keywords using Gemini LLM"""
    try:
        prompt = f"""
        Please extract the most important search keywords from the following auction item description.
        
        Title: {title}
        Description: {description}
        
        Rules:
        1. Return 10-40 relevant keywords and expressions (depending on the length and detail of the description)
        2. Use both English and original language words if relevant
        3. Include brand, type, condition, color, size information
        4. Add synonyms as well (e.g. car, vehicle, automobile)
        5. Return only comma-separated words, no other formatting
        6. Do not include advertising phrases or unnecessary words like "for sale", "auction", "buy now", etc.
        
        Example output: volkswagen, beetle, car, vehicle, vintage, classic, 1970, german, bug, retro, grey
        """
        
        response = gemini_model.generate_content(prompt)
        keywords_text = response.text.strip()
        
        keywords = [kw.strip().lower() for kw in keywords_text.split(',') if kw.strip()]
        
        logger.info(f"Gemini keywords generated: {len(keywords)} items - {keywords[:5]}...")
        return keywords
        
    except Exception as e:
        logger.error(f"Error in Gemini keyword extraction: {e}")
        words = f"{title} {description}".lower().split()
        return list(set([w for w in words if len(w) > 3]))[:10]

def create_enriched_text(title: str, description: str, keywords: List[str]) -> str:
    keywords_text = " ".join(keywords)
    return f"{title} {description} {keywords_text}"

def get_embedding(text: str) -> List[float]:
    try:
        response = requests.post(
            f"{EMBEDDING_SERVICE_URL}/embed",
            json={"text": text},
            timeout=30
        )
        response.raise_for_status()
        
        data = response.json()
        return data["embedding"]
        
    except requests.exceptions.RequestException as e:
        logger.error(f"Error communicating with embedding service: {e}")
        raise
    except KeyError as e:
        logger.error(f"Missing field in embedding response: {e}")
        raise

def get_batch_embeddings(texts: List[str]) -> List[List[float]]:
    try:
        response = requests.post(
            f"{EMBEDDING_SERVICE_URL}/embed/batch",
            json={"texts": texts},
            timeout=60
        )
        response.raise_for_status()
        
        data = response.json()
        return data["embeddings"]
        
    except requests.exceptions.RequestException as e:
        logger.error(f"Error with batch embedding service communication: {e}")
        raise
    except KeyError as e:
        logger.error(f"Missing field in batch embedding response: {e}")
        raise

@app.route('/health', methods=['GET'])
def health_check():
    try:
        qdrant_connected = False
        collection_exists = False
        total_vectors = None
        embedding_service_status = False
        
        try:
            collections = qdrant_client.get_collections()
            qdrant_connected = True
            
            collection_names = [col.name for col in collections.collections]
            collection_exists = COLLECTION_NAME in collection_names
            
            if collection_exists:
                collection_info = qdrant_client.get_collection(COLLECTION_NAME)
                total_vectors = collection_info.vectors_count
                
        except Exception as e:
            logger.warning(f"Qdrant ellenőrzési hiba: {e}")
        
        try:
            response = requests.get(f"{EMBEDDING_SERVICE_URL}/health", timeout=5)
            embedding_service_status = response.status_code == 200
        except Exception as e:
            logger.warning(f"Embedding service ellenőrzési hiba: {e}")
        
        status = "healthy" if (qdrant_connected and embedding_service_status) else "unhealthy"
        
        return jsonify({
            "status": status,
            "service": "search-service",
            "qdrant_connected": qdrant_connected,
            "collection_exists": collection_exists,
            "total_vectors": total_vectors,
            "embedding_service_connected": embedding_service_status
        })
        
    except Exception as e:
        logger.error(f"Health check hiba: {e}")
        return jsonify({
            "status": "unhealthy",
            "error": str(e)
        }), 500

@app.route('/index', methods=['POST'])
def index_product():
    try:
        data = request.get_json()
        
        if not data:
            return jsonify({"error": "JSON data required"}), 400
        
        required_fields = ['title', 'description', 'category']
        for field in required_fields:
            if field not in data:
                return jsonify({"error": f"Missing field: {field}"}), 400
        
        title = data['title']
        description = data['description']
        category = data['category']
        product_id_str = data.get('product_id', str(uuid.uuid4()))
        
        try:
            product_id = int(product_id_str)
        except (ValueError, TypeError):
            product_id = int(str(uuid.uuid4()).replace('-', '')[:8], 16)
            
        price = data.get('price')
        
        logger.info(f"Extracting keywords for product: {product_id} (original: {product_id_str})")
        keywords = extract_keywords_with_gemini(title, description)
        
        enriched_text = create_enriched_text(title, description, keywords)
        
        logger.info(f"Generating embedding for product: {product_id}")
        embedding = get_embedding(enriched_text)
        
        payload = {
            "title": title,
            "description": description,
            "category": category,
            "price": price,
            "keywords": keywords,
            "enriched_text": enriched_text
        }
        
        qdrant_client.upsert(
            collection_name=COLLECTION_NAME,
            points=[
                models.PointStruct(
                    id=product_id,
                    vector=embedding,
                    payload=payload
                )
            ]
        )
        
        logger.info(f"Product successfully indexed: {product_id}")
        return jsonify({
            "status": "success",
            "message": f"Product '{product_id}' successfully indexed",
            "product_id": product_id
        })
        
    except Exception as e:
        logger.error(f"Error indexing product: {e}")
        return jsonify({
            "error": "Product indexing failed",
            "message": str(e)
        }), 500

@app.route('/search', methods=['POST'])
def search_products():
    try:
        data = request.get_json()
        
        if not data or 'query' not in data:
            return jsonify({"error": "Missing 'query' field"}), 400
        
        query = data['query']
        limit = data.get('limit', 100)
        score_threshold = data.get('score_threshold', 0.05)  
        category_filter = data.get('category_filter')
        
        logger.info(f"Executing search: {query}")
        search_embedding = get_embedding(query)
        
        search_filter = None
        if category_filter:
            search_filter = models.Filter(
                must=[
                    models.FieldCondition(
                        key="category",
                        match=models.MatchValue(value=category_filter)
                    )
                ]
            )
        
        search_results = qdrant_client.search(
            collection_name=COLLECTION_NAME,
            query_vector=search_embedding,
            limit=limit,
            score_threshold=score_threshold,
            query_filter=search_filter,
            with_payload=True
        )
        
        logger.info(f"Search found {len(search_results)} results above threshold {score_threshold}")
        
        # Debug: search without threshold to see all results
        debug_results = qdrant_client.search(
            collection_name=COLLECTION_NAME,
            query_vector=search_embedding,
            limit=10,
            score_threshold=0.0,  # No threshold for debug
            query_filter=search_filter,
            with_payload=True
        )
        logger.info(f"Debug: All results (no threshold): {[(hit.id, hit.score, hit.payload.get('title', 'No title')[:50] if hit.payload else 'No payload') for hit in debug_results]}")
        
        results = []
        for hit in search_results:
            result = {
                "id": str(hit.id),
                "score": hit.score
            }
            results.append(result)
        
        return jsonify({
            "query": query,
            "auction_ids": [r["id"] for r in results],
            "scores": [r["score"] for r in results],
            "total_found": len(results),
            "limit": limit,
            "score_threshold": score_threshold
        })
        
    except Exception as e:
        logger.error(f"Error during search: {e}")
        return jsonify({
            "error": "Search error",
            "message": str(e)
        }), 500

@app.route('/collections/<collection_name>/info', methods=['GET'])
def get_collection_info(collection_name: str):
    try:
        if collection_name != COLLECTION_NAME:
            return jsonify({"error": "Collection not found"}), 404
        
        collection_info = qdrant_client.get_collection(collection_name)
        return jsonify({
            "name": collection_name,
            "vectors_count": collection_info.vectors_count,
            "segments_count": collection_info.segments_count,
            "status": collection_info.status
        })
        
    except Exception as e:
        logger.error(f"Collection info query error: {e}")
        return jsonify({
            "error": "Failed to retrieve collection information",
            "message": str(e)
        }), 500

@app.route('/batch_index', methods=['POST'])
def batch_index_products():
    try:
        data = request.get_json()
        
        if not data or 'products' not in data:
            return jsonify({"error": "Missing 'products' field"}), 400
        
        products = data['products']
        
        if not isinstance(products, list):
            return jsonify({"error": "'products' must be a list"}), 400
        
        texts_to_encode = []
        product_data = []
        
        for product in products:
            required_fields = ['title', 'description', 'category']
            for field in required_fields:
                if field not in product:
                    return jsonify({"error": f"Missing field in product: {field}"}), 400
            
            keywords = extract_keywords_with_gemini(product['title'], product['description'])
            enriched_text = create_enriched_text(product['title'], product['description'], keywords)
            texts_to_encode.append(enriched_text)
            
            product_id_str = product.get('product_id', str(uuid.uuid4()))
            try:
                product_id = int(product_id_str)
            except (ValueError, TypeError):
                product_id = int(str(uuid.uuid4()).replace('-', '')[:8], 16)
            
            product_info = {
                'product_id': product_id,
                'product_id_str': product_id_str,
                'title': product['title'],
                'description': product['description'],
                'category': product['category'],
                'price': product.get('price'),
                'keywords': keywords,
                'enriched_text': enriched_text
            }
            product_data.append(product_info)
        
        logger.info(f"Generating batch embeddings for {len(products)} products")
        embeddings = get_batch_embeddings(texts_to_encode)
        
        points = []
        for i, (embedding, product_info) in enumerate(zip(embeddings, product_data)):
            payload = {
                "title": product_info['title'],
                "description": product_info['description'],
                "category": product_info['category'],
                "price": product_info['price'],
                "keywords": product_info['keywords'],
                "enriched_text": product_info['enriched_text']
            }
            
            point = models.PointStruct(
                id=product_info['product_id'],
                vector=embedding,
                payload=payload
            )
            points.append(point)
        
        qdrant_client.upsert(
            collection_name=COLLECTION_NAME,
            points=points
        )
        
        logger.info(f"{len(products)} products successfully indexed")
        return jsonify({
            "status": "success",
            "message": f"{len(products)} products successfully indexed",
            "indexed_products": [p['product_id'] for p in product_data]
        })
        
    except Exception as e:
        logger.error(f"Error in batch indexing: {e}")
        return jsonify({
            "error": "Batch indexing failed",
            "message": str(e)
        }), 500

if __name__ == "__main__":
    try:
        ensure_collection_exists()
        logger.info("Search service successfully initialized")
    except Exception as e:
        logger.error(f"Initialization error: {e}")
        exit(1)
    
    app.run(host='0.0.0.0', port=8001, debug=True)