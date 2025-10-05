from flask import Flask, jsonify, request
from sentence_transformers import SentenceTransformer
import numpy as np
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)

# Load the pre-trained model
model = SentenceTransformer('all-MiniLM-L6-v2')
logger.info("Model loaded successfully. Model used is 'all-MiniLM-L6-v2'.")

@app.route('/health', methods=['GET'])
def health_check():
    """Just to check if the service is up and running."""
    return jsonify({
        "status": "healthy",
        "service": "embedding-service",
        "model": "all-MiniLM-L6-v2",
        "multilingual": True
    })

@app.route('/embed', methods=['POST'])
def embed_text():
    """Create embeddings for the provided text."""
    try:
        data = request.get_json()
        if not data or 'text' not in data:
            return jsonify({"error": "Invalid input, 'text' field is required."}), 400
        
        text = data['text']

        if not text.strip():
            return jsonify({"error": "Text cannot be empty"}), 400
        
        logger.info(f"Generating embedding for text: {text[:30]}...")  # Log first 30 chars
        embedding = model.encode([text])
        embedding_list = embedding[0].tolist()

        return jsonify({
            "text": text,
            "embedding": embedding_list,
            "dimensions": len(embedding_list),
            "model": "all-MiniLM-L6-v2"
        })
    
    except Exception as e:
        logger.error(f"Error generating embedding: {str(e)}")
        return jsonify({
            "error": "Internal server error",
            "message": str(e)
        }), 500

@app.route('/embed/batch', methods=['POST'])
def create_batch_embedding():
    """Több szöveg embedding-je egyszerre (hatékonyabb)"""
    try:
        data = request.get_json()
        
        if not data or 'texts' not in data:
            return jsonify({
                "error": "Missing 'texts' field in request body"
            }), 400
        
        texts = data['texts']
        
        if not isinstance(texts, list):
            return jsonify({
                "error": "'texts' must be a list"
            }), 400
        
        logger.info(f"Batch embedding készítése {len(texts)} szöveghez")
        embeddings = model.encode(texts)
        
        embeddings_list = [emb.tolist() for emb in embeddings]
        
        return jsonify({
            "texts": texts,
            "embeddings": embeddings_list,
            "count": len(embeddings_list),
            "dimensions": len(embeddings_list[0]) if embeddings_list else 0
        })
        
    except Exception as e:
        logger.error(f"Batch hiba: {str(e)}")
        return jsonify({
            "error": "Internal server error",
            "message": str(e)
        }), 500

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5001, debug=True)