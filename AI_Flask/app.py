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
        FIRST: Start your response with "NO" on the first line if the photos are insufficient (too blurry, poor lighting, missing important angles, too few photos, etc.)
        
        IF NO: Write only "No" and nothing else.

        IF YES: Create a compelling auction-style description as if you're the seller trying to attract buyers.

        For auction descriptions, write in a natural, marketing-oriented style that includes overview, condition, key features, completeness, target buyer well integrated into the text. 
        Do not make it sound like an enumeration or list or bullet point.
        Mainly try to enhance the positive aspects of the item while being honest about any flaws.

        Write like an experienced seller who knows how to highlight positives while being honest about flaws. Use engaging language that would make potential bidders interested, but remain truthful and detailed.
        
        Example opening styles:
        - "Up for auction is this beautiful..."
        - "Offered here is a well-maintained..."
        - "Don't miss this opportunity to own..."
        
        Keep it conversational but professional, like a knowledgeable seller describing their item to a potential buyer.
        """

        response = model.generate_content([prompt, *image_parts])

        if response.text.strip().lower().startswith('no'):
            return jsonify({"description": "The amount of photos and angles are insufficient, please provide more."}), 200
        
        return jsonify({"description": response.text})

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)
