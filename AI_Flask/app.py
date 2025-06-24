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
                Analyze the item(s) shown in the uploaded images and generate a detailed, professional description suitable for auction or marketplace listings. The output should be a natural, multi-paragraph narrative rather than a bullet list.
                In your analysis, seamlessly cover aspects such as:
                Overall condition (e.g., excellent, good, fair, poor)
                Visible defects (scratches, cracks, chips, dents, etc.)
                Cleanliness and visual impression (stains, dust, discoloration)
                Usage level / Quality grading (new, like new, used-good, used-fair, vintage/collector's item)
                Notable or rare features (signatures, markings, collectible value)
                Emphasize strengths while honestly acknowledging flaws that may affect value. The goal is to give buyers a trustworthy, clear understanding of the item.
                If any critical details are not visible (e.g., missing views of the back, base, interior, etc.), politely mention that additional photos may be needed to fully assess the item.
                Please ensure that, the description is written in a way, that would fit for an auction page item descripton, written by the seller.
                If you think the photoes are not enough, then only write "description not possible, please upload more photos of the item(s)!".
                Please do not write anything else, just the description.
           """

        response = model.generate_content([prompt, *image_parts])
        return jsonify({"description": response.text})

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)
