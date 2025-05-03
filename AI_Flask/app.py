from flask import Flask, request, jsonify
import os
import tempfile
from werkzeug.utils import secure_filename
import google.generativeai as genai
from dotenv import load_dotenv

load_dotenv()
app = Flask(__name__)

#Gemini set-up (using gemini-pro-vision now, might change to an open-source model later)
genai.configure(api_key=os.getenv("GOOGLE_API_KEY"))
model = genai.GenerativeModel('gemini-2.0-flash')

ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg', 'gif'}
ALLOWED_ORIGINS = ["http://localhost:8081"]

def checking_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.after_request
def add_cors(response):
    if request.headers.get("Origin") in ALLOWED_ORIGINS:
        response.headers.add("Access-Control-Allow-Origin", request.headers.get("Origin"))
        response.headers.add("Access-Control-Allow-Methods", "POST")
    return response

@app.route('/generate-description', methods=['POST'])
def generate_description():
    if 'images' not in request.files:
        return jsonify({"error": "No pictures were sent!"}), 400

    files = request.files.getlist('images')
    if not files or all(file.filename == '' for file in files):
        return jsonify({"error": "No valid pictures or picture names!"}), 400

    try:
        image_parts = []
        for file in files:
            if file and checking_file(file.filename):
                image_parts.append({
                    "mime_type": file.mimetype,
                    "data": file.read()
                })

        if not image_parts:
            return jsonify({"error": "Invalid pictures!"}), 400

        prompt = """
        Analyze the uploaded images and generate a detailed description suitable for auction listings. Focus specifically on:
            - **Overall Condition** (Excellent, Good, Fair, Poor)
            - **Visible Defects** (scratches, cracks, chips, dents)
            - **Cleanliness** (stains, dirt, dust, discoloration)
            - **Quality Assessment** (New, Used-Like New, Used-Good, Used-Fair, Vintage/Collector's Item)
            - **Notable Features** (unique markings, signatures, rarity indicators)

            Provide a professional, objective description that helps potential buyers evaluate the item accurately. 
            Be specific about any imperfections while maintaining a neutral tone. 
            Highlight both positive attributes and any flaws that affect value.
        """

        response = model.generate_content([prompt, *image_parts])
        return jsonify({"description": response.text})

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)
