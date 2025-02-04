from flask import Blueprint, render_template, request, redirect, url_for, abort
import requests
from datetime import datetime

itinerario = Blueprint('itinerario', __name__)
URL_STATIC = 'http://localhost:8090/api/itinerario'
@itinerario.route('/', methods = ['GET'])
def home():
    r = requests.get(f"{URL_STATIC}/list")
    data = r.json()
    itinerarios = data['data']
    return render_template('/itinerario_templates/itinerario_list.html', itinerarios = itinerarios)

@itinerario.route('/search/<attribute>/<value>', methods = ['GET'])
def search(attribute, value):
    r = requests.get(f"{URL_STATIC}/search/{attribute}/{value}")
    print(r.text)
    print(r.status_code)
    response = r.json()
    data = response.get('data',[])
    if isinstance(data, dict):
        data = [data]
    return render_template('/itinerario_templates/itinerario_list.html', itinerarios = data)

@itinerario.route('/order/<attribute>/<type>', methods = ['GET'])
def order(attribute, type):
    r = requests.get(f"{URL_STATIC}/order/{attribute}/{type}")
    response = r.json()
    data = response.get('data',[])
    return render_template('/itinerario_templates/itinerario_list.html', itinerarios = data)

@itinerario.route('/<id>', methods=['GET'])
def itinerario_detail(id):
    try:
        r = requests.get(f"{URL_STATIC}/get/{id}")
        r.raise_for_status()
        data = r.json()
        itinerario = data["data"]
        return render_template('/itinerario_templates/itinerario_detail.html', itinerario=itinerario)
    except requests.RequestException as e:
        return redirect(url_for('itinerario.home'))

@itinerario.route('/<id>/delete', methods=['GET'])
def delete(id):
    try:
        r = requests.delete(f"{URL_STATIC}/{id}/delete")
        r.raise_for_status()
        return redirect(url_for('itinerario.home'))
    except requests.RequestException:
        return redirect(url_for('itinerario.home'))


@itinerario.route('/<id>/edit', methods=['GET'])
def edit(id):
    try:
        r = requests.get(f"{URL_STATIC}/get/{id}")
        r.raise_for_status()
        data = r.json()
        itinerario = data["data"]

        lista_estados = get_options(f"{URL_STATIC}/listType")

        print(r.status_code, r.text)
        return render_template('/itinerario_templates/edit_itinerario.html', itinerario=itinerario, estados=lista_estados)
    except requests.RequestException:
        return redirect(url_for('itinerario.home'))

@itinerario.route('/<id>/edit', methods=['POST'])
def update(id):
    try:
        data = {
            "id": request.form.get('id'),
            "estado": request.form.get('estado'), 
            "idConductorAsignado": int(request.form.get('conductor-asignado')),
            "horaIncio": request.form.get('horaInicio'),
            "duracionEstimada": request.form.get('duracionEstimada'),
        }

        print(data)
        r = requests.post(f"{URL_STATIC}/update", json=data)
        r.raise_for_status()

        return redirect(url_for('itinerario.home'))
    except requests.RequestException:
        return redirect(url_for('itinerario.edit', id=id))

@itinerario.route('/save', methods=['GET'])
def save_form():
    lista_estados = get_options(f"{URL_STATIC}/listType")
    itinerario = {
        "estado": "",
        "conductor-asignado": "",
        "horaInicio": "",
        "duracionEstimada": "",
    }
    return render_template('/itinerario_templates/registrar_itinerario.html', itinerario=itinerario, estados=lista_estados)

@itinerario.route('/save', methods=['POST'])
def save():
    try:
        data = {
            "estado": request.form.get('estado'), 
            "conductor-asignado": request.form.get('conductor-asignado'),
            "horaInicio": request.form.get('horaInicio'),
            "duracionEstimada": request.form.get('duracionEstimada'),
        }
        print(data)
        r = requests.post(f"{URL_STATIC}/save", json=data)
        r.raise_for_status()

        return redirect(url_for('itinerario.home'))
    except requests.RequestException as e:
        return redirect(url_for('itinerario.save_form'))


def get_options(endpoint):
    try:
        r = requests.get(endpoint)
        r.raise_for_status()
        data = r.json()
        options = [(item, format_string(item.replace("_", " "), capitalizar_palabras=True)) for item in data.get("data", [])]
        return options
    except requests.RequestException as e:
        print(f'Error al obtener opciones desde {endpoint}: {e}')
        return []

def format_string(cadena, capitalizar_palabras=False):
    resultado = ''.join([' ' + char if char.isupper() and (i > 0 and not cadena[i - 1].isupper()) else char
                         for i, char in enumerate(cadena)])
    if capitalizar_palabras:
        return ' '.join([palabra.capitalize() for palabra in resultado.split()])
    else:
        return resultado.capitalize()
