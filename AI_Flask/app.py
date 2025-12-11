from flask import Flask, request, jsonify
import os
import tempfile
from werkzeug.utils import secure_filename
import google.generativeai as genai
from dotenv import load_dotenv

load_dotenv()
app = Flask(__name__)

# Gemini set-up (using gemini-2.5-flash now, might change later)
genai.configure(api_key=os.getenv("GOOGLE_API_KEY"))
model = genai.GenerativeModel('gemini-2.5-flash')

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
        CRITICAL FIRST STEP - IMAGE QUALITY CHECK:
        Respond with "NO" on the first line if ANY of these conditions are true:
        - Photos are blurry, dark, or poorly lit
        - Missing essential angles (front, back, sides, details)
        - Cannot clearly identify what the item is
        - Item is partially obscured or cut off
        - Photos are too small or low resolution
        - Cannot assess the item's actual condition from the photos
        - Multiple items shown without clear focus on one
        - Stock photos or screenshots instead of actual product photos
        
        BE STRICT: When in doubt, respond "NO". It's better to ask for more photos than to guess.
        
        IF NO: Write only "NO" and nothing else. No explanations.

        IF the photos are SUFFICIENT (clear, multiple angles, good lighting, can assess condition):
        Analyze the images and provide a JSON response with the following structure:
        {
            "description": "compelling auction description here",
            "category": "category name",
            "itemState": "item condition status",
            "condition": condition_number
        }

        For the description: Create a compelling auction-style description as if you're the seller trying to attract buyers.
        Write in a natural, marketing-oriented style that includes overview, condition, key features, completeness, target buyer well integrated into the text. 
        Do not make it sound like an enumeration or list or bullet point.
        Mainly try to enhance the positive aspects of the item while being honest about any flaws.

        Write like an experienced seller who knows how to highlight positives while being honest about flaws. Use engaging language that would make potential bidders interested, but remain truthful and detailed.
        
        Example opening styles:
        - "Up for auction is this beautiful..."
        - "Offered here is a well-maintained..."
        - "Don't miss this opportunity to own..."

        For category: Suggest a general category name based on what the item is. Use one category from these EXACT options:
            Elektronika
            Ékszerek és Órák
            Művészet és Gyűjteményes tárgyak
            Bútor
            Járművek
            Ruházat és Kiegészítők
            Sport és Szabadtéri
            Játékok és Játéktermi játékok
            Otthon és Kert
            Könyvek és Médiatartalmak
            Antikvitás
            Érmék és Bélyegek
            Bor és Alkoholos italok
            Hangszerek
            Irodai felszerelés
        
        For itemState: Choose ONE from these exact options based on the item's condition visible in the photos:
        - "Brand new" (unopened, pristine, with original packaging)
        - "Like new" (lightly used, excellent condition, minimal signs of use)
        - "Lightly used" (good condition, some minor wear)
        - "Well used" (functional but shows clear signs of use)
        - "Heavily used" (significant wear but still functional)
        
        For condition: Provide a number from 1 to 100 representing the item's condition:
        - 90-100: Perfect/Brand new condition
        - 70-89: Excellent/Like new condition
        - 50-69: Good/Lightly used condition
        - 30-49: Fair/Well used condition
        - 1-29: Poor/Heavily used condition

        IMPORTANT: Return ONLY valid JSON without any markdown formatting. 
        Do NOT wrap the JSON in ```json or ``` code blocks.
        Return the raw JSON object directly.
        """

        response = model.generate_content([prompt, *image_parts])

        if response.text.strip().lower().startswith('no'):
            return jsonify({"description": "The amount of photos and angles are insufficient, please provide more."}), 200
        
        # Parse the JSON response
        import json
        import re
        
        try:
            # Clean up the response text - remove all possible formatting
            cleaned_text = response.text.strip()
            
            # Remove YES/YES\n at the beginning if present
            cleaned_text = re.sub(r'^YES\s*\n*', '', cleaned_text, flags=re.IGNORECASE)
            
            # Remove markdown code blocks (```json and ```)
            cleaned_text = re.sub(r'```json\s*', '', cleaned_text)
            cleaned_text = re.sub(r'```\s*', '', cleaned_text)
            
            # Find JSON object between { and }
            json_match = re.search(r'\{.*\}', cleaned_text, re.DOTALL)
            if json_match:
                cleaned_text = json_match.group(0)
            
            cleaned_text = cleaned_text.strip()
            
            print(f"Cleaned text: {cleaned_text[:200]}...")
            
            result = json.loads(cleaned_text)
            return jsonify(result)
        except json.JSONDecodeError as e:
            print(f"JSON parsing error: {e}")
            print(f"Raw response: {response.text}")
            # Fallback if AI doesn't return proper JSON
            return jsonify({
                "description": response.text,
                "category": "",
                "itemState": "Lightly used",
                "condition": 50
            })

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)
