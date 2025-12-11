"""
Embedding Service - Szöveg vektorizáló szolgáltatás
A sentence-transformers modellt használja embedding generáláshoz.
"""

from flask import Flask, jsonify, request
from sentence_transformers import SentenceTransformer
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)

model = SentenceTransformer('all-MiniLM-L6-v2')
logger.info("Model loaded: all-MiniLM-L6-v2")


@app.route('/embed', methods=['POST'])
def embed_text():
    """Create embedding for the provided text."""
    try:
        data = request.get_json()
        
        if not data or 'text' not in data:
            return jsonify({"error": "'text' field is required"}), 400
        
        text = data['text']
        
        if not text.strip():
            return jsonify({"error": "Text cannot be empty"}), 400
        
        logger.info(f"Generating embedding for: {text[:50]}...")
        embedding = model.encode([text])[0].tolist()
        
        return jsonify({"embedding": embedding})
    
    except Exception as e:
        logger.error(f"Error generating embedding: {e}")
        return jsonify({
            "error": "Embedding generation failed",
            "message": str(e)
        }), 500


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5001, debug=True)