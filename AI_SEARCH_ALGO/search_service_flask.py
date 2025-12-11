"""
Search Service - Qdrant alapú vektorkereső szolgáltatás
Ez a szolgáltatás a Qdrant vektordatabázissal kommunikál
és az embedding service-t használja a szövegek vektorizálásához.
"""

from dotenv import load_dotenv
load_dotenv()

from flask import Flask, jsonify, request
from qdrant_client import QdrantClient
from qdrant_client.http import models
import requests
import logging
import os
import google.generativeai as genai
from typing import List

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)

QDRANT_HOST = "localhost"
QDRANT_PORT = 6333
EMBEDDING_SERVICE_URL = "http://localhost:5001"
COLLECTION_NAME = "auction_products"
VECTOR_SIZE = 384  # all-MiniLM-L6-v2 model vector size

genai.configure(api_key=os.getenv("GOOGLE_API_KEY"))
gemini_model = genai.GenerativeModel('gemini-2.5-flash')

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

@app.route('/index', methods=['POST'])
def index_product():
    try:
        data = request.get_json()
        
        if not data:
            return jsonify({"error": "JSON data required"}), 400
        
        required_fields = ['product_id', 'title', 'description', 'category']
        for field in required_fields:
            if field not in data:
                return jsonify({"error": f"Missing field: {field}"}), 400
        
        product_id = data['product_id']
        title = data['title']
        description = data['description']
        category = data['category']
        
        if not isinstance(product_id, int):
            return jsonify({"error": "product_id must be an integer"}), 400
        
        logger.info(f"Extracting keywords for product: {product_id}")
        keywords = extract_keywords_with_gemini(title, description)
        
        enriched_text = create_enriched_text(title, description, keywords)
        
        logger.info(f"Generating embedding for product: {product_id}")
        embedding = get_embedding(enriched_text)
        
        payload = {
            "title": title,
            "description": description,
            "category": category,
            "keywords": keywords
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
        score_threshold = data.get('score_threshold', 0.12)
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
            with_payload=False
        )
        
        logger.info(f"Search found {len(search_results)} results above threshold {score_threshold}")
        
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


@app.route('/delete', methods=['DELETE'])
def delete_product():
    """Delete a product from the search index"""
    try:
        data = request.get_json()
        
        if not data or 'product_id' not in data:
            return jsonify({"error": "Missing 'product_id' field"}), 400
        
        product_id = data['product_id']
        
        if not isinstance(product_id, int):
            return jsonify({"error": "product_id must be an integer"}), 400
        
        qdrant_client.delete(
            collection_name=COLLECTION_NAME,
            points_selector=models.PointIdsList(points=[product_id])
        )
        
        logger.info(f"Product deleted from index: {product_id}")
        return jsonify({
            "status": "success",
            "message": f"Product '{product_id}' deleted from index",
            "product_id": product_id
        })
        
    except Exception as e:
        logger.error(f"Error deleting product: {e}")
        return jsonify({
            "error": "Product deletion failed",
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