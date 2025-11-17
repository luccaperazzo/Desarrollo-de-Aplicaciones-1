import os
import tempfile
import google.generativeai as genai
from flask import Flask, request, jsonify
import json

# --- Configuración Segura ---
try:
    api_key = os.environ.get("GOOGLE_API_KEY")
    if not api_key:
        raise ValueError("La variable de entorno GOOGLE_API_KEY no está configurada.")
    genai.configure(api_key=api_key)
    print("Google API Key configured successfully.")
except Exception as e:
    print(f"Error crítico en la configuración de la API: {e}")
    raise e

app = Flask(__name__)

GEMINI_PROMPT = '''
Eres un asistente culinario experto. Analiza el archivo de audio adjunto que contiene a una persona dictando los pasos de una receta.
Tu tarea es convertir el discurso en una lista estructurada de pasos claros y concisos de la receta.

Tu salida DEBE ser un objeto JSON que contenga una única clave "steps", cuyo valor sea un arreglo (array) de cadenas de texto (strings).
No incluyas ningún otro texto, explicación ni formato markdown como ```json.

Ejemplo:
Si el audio dice “primero cortas una cebolla y luego la fríes en una sartén”, tu salida exacta debe ser:

{
  "steps": ["Cortar la cebolla", "Freír la cebolla en una sartén"]
}
'''

# CORREGIDO: La ruta ahora es la raíz. Vercel se encargará del enrutamiento público.
@app.route("/", methods=["POST"])
def transcribe_audio_route():
    if 'audio' not in request.files:
        return jsonify({"error": "No se encontró el archivo de audio"}), 400

    audio_file = request.files['audio']
    uploaded_file = None
    temp_file_path = None

    try:
        with tempfile.NamedTemporaryFile(delete=False, suffix='.wav') as temp_file:
            audio_file.save(temp_file.name)
            temp_file_path = temp_file.name
        
        print("Subiendo archivo de audio a la API de Gemini...")
        uploaded_file = genai.upload_file(
            path=temp_file_path,
            display_name="recipe-audio-input"
        )
        print(f"Archivo subido con éxito: {uploaded_file.name}")

        print("Generando contenido con Gemini...")
        model = genai.GenerativeModel('models/gemini-1.5-flash-latest')
        
        response = model.generate_content(
            [GEMINI_PROMPT, uploaded_file],
            generation_config=genai.types.GenerationConfig(
                response_mime_type="application/json"
            )
        )
        json_string = response.text
        print(f"Respuesta de Gemini (JSON String): {json_string}")

        data_dict = json.loads(json_string)
        return jsonify(data_dict)
    
    except Exception as e:
        print(f"Ocurrió un error: {e}")
        return jsonify({"error": "Error procesando el audio con Gemini"}), 500
    
    finally:
        # Limpieza de archivos temporales
        if uploaded_file:
            try:
                print(f"Eliminando archivo subido: {uploaded_file.name}")
                genai.delete_file(uploaded_file.name)
            except Exception as e:
                print(f"Error al eliminar el archivo de Gemini: {e}")

        if temp_file_path and os.path.exists(temp_file_path):
            print(f"Eliminando archivo temporal: {temp_file_path}")
            os.unlink(temp_file_path)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
