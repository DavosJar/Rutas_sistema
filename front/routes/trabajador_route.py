from flask import Blueprint, render_template, request, redirect, url_for, flash, session,jsonify
import requests
from datetime import datetime

import app

trabajador = Blueprint('trabajador', __name__)
URL_STATIC = 'http://localhost:8090/api/trabajador'
app.secret_key = 'tu_clave_secreta_aqui'

def get_auth_headers():
    auth_token = session.get('token')
    if not auth_token:
        flash('Inicia sesión para continuar.', 'error')
        return None
    return {'Authorization': f'Bearer {auth_token}'}

@trabajador.route('/<int:page>', methods=['GET'])
@trabajador.route('/', methods=['GET'])
def home(page=1):
    headers = get_auth_headers()
    if not headers:
        return redirect(url_for('auth.login'))

    token = session.get('token')
    if not token:
        flash('No tienes acceso. Inicia sesión primero.', 'error')
        return redirect(url_for('auth.login'))

    headers = {'Authorization': f'Bearer {token}'}
    r = requests.get(f"{URL_STATIC}/list/{page}", headers=headers)

    if r.status_code == 200:
        data = r.json()
        trabajadores = data.get('data')
        total_items = data.get('totalItems', 0)
        items_per_page = data.get('itemsPerPage', 20)
        total_pages = (total_items // items_per_page) + (1 if total_items % items_per_page > 0 else 0)
        
        return render_template('/trabajador_templates/trabajador_list.html', 
                               trabajadores=trabajadores, 
                               page=page, 
                               total_pages=total_pages)
    else:
        flash('Error al obtener la lista de trabajadores', 'error')
        return redirect(url_for('trabajador.home'))
        
@trabajador.route('/search/<attribute>/<value>', methods=['GET'])
def search(attribute, value):
    token = session.get('token')
    if not token:
        flash('No tienes acceso. Inicia sesión primero.', 'error')
        return redirect(url_for('auth.login'))

    headers = {'Authorization': f'Bearer {token}'}
    r = requests.get(f"{URL_STATIC}/list/search/{attribute}/{value}", headers=headers)
    if r.status_code == 200:
        response = r.json()
        data = response.get('data', [])
        if isinstance(data, dict):
            data = [data]
        return render_template('/trabajador_templates/trabajador_list.html', trabajadores=data)
    else:
        flash('Error al buscar trabajadores', 'error')
        return redirect(url_for('trabajador.home'))

@trabajador.route('/info/<id>', methods=['GET'])
def trabajador_detail(id):
    token = session.get('token')
    if not token:
        flash('No tienes acceso. Inicia sesión primero.', 'error')
        return redirect(url_for('auth.login'))

    headers = {'Authorization': f'Bearer {token}'}
    try:
        r = requests.get(f"{URL_STATIC}/get/{id}", headers=headers)
        r.raise_for_status()
        data = r.json()
        print(data)
        trabajador = data["data"]
        return render_template('/trabajador_templates/trabajador_detail.html', trabajador=trabajador)
    except requests.RequestException as e:
        return render_template('/error.html' )

@trabajador.route('/<id>/delete', methods=['GET'])
def delete(id):
    token = session.get('token')
    if not token:
        flash('No tienes acceso. Inicia sesión primero.', 'error')
        return redirect(url_for('auth.login'))

    headers = {'Authorization': f'Bearer {token}'}
    try:
        r = requests.delete(f"{URL_STATIC}/{id}/delete", headers=headers)
        r.raise_for_status()
        flash('Trabajador eliminado correctamente', 'success')
    except requests.RequestException as e:
        flash('Error al eliminar el trabajador', 'error')
    return redirect(url_for('trabajador.home'))

@trabajador.route('/order/<attribute>/<type>', methods=['GET'])
def order(attribute, type):
    token = session.get('token')
    if not token:
        flash('No tienes acceso. Inicia sesión primero.', 'error')
        return redirect(url_for('auth.login'))

    headers = {'Authorization': f'Bearer {token}'}
    r = requests.get(f"{URL_STATIC}/list/order/{attribute}/{type}", headers=headers)
    if r.status_code == 200:
        response = r.json()
        data = response.get('data', [])
        return render_template('/trabajador_templates/trabajador_list.html', trabajadores=data)
    else:
        flash('Error al ordenar los trabajadores', 'error')
        return redirect(url_for('trabajador.home'))

@trabajador.route('/<id>/edit', methods=['GET'])
def edit(id):
    token = session.get('token')
    if not token:
        flash('No tienes acceso. Inicia sesión primero.', 'error')
        return redirect(url_for('auth.login'))

    headers = {'Authorization': f'Bearer {token}'}
    try:
        r = requests.get(f"{URL_STATIC}/get/{id}", headers=headers)
        r.raise_for_status()
        data = r.json()
        trabajador = data["data"]

        if trabajador.get("fechaNacimiento"):
            try:
                trabajador["fechaNacimiento"] = datetime.strptime(trabajador["fechaNacimiento"], "%Y-%m-%d").strftime("%Y-%m-%d")
            except ValueError:
                trabajador["fechaNacimiento"] = ""

        sexo = get_options(f'{URL_STATIC}/sexo', headers)
        tipo = get_options(f'{URL_STATIC}/tipo_identificacion', headers)

        return render_template('trabajador_templates/edit_trabajador.html', trabajador=trabajador, sexo=sexo, tipoIdentificacion=tipo, errors={})
    except requests.RequestException as e:
        flash('Error al obtener los datos del trabajador', 'error')
        return redirect(url_for('trabajador.home'))

@trabajador.route('/<id>/edit', methods=['POST'])
def update(id):
    token = session.get('token')
    if not token:
        flash('No tienes acceso. Inicia sesión primero.', 'error')
        return redirect(url_for('auth.login'))

    headers = {'Authorization': f'Bearer {token}'}
    try:
        fechaNacimiento = request.form.get('fechaNacimiento')
        if fechaNacimiento:
            try:
                fechaNacimiento = datetime.strptime(fechaNacimiento, "%Y-%m-%d").strftime("%Y-%m-%d")
            except ValueError:
                flash('Fecha de nacimiento inválida. Debe estar en el formato AAAA-MM-DD.', 'error')
                return redirect(url_for('trabajador.edit', id=id))

        data = {
            "id": int(id),  
            "nombre": request.form.get('nombre'),
            "apellido": request.form.get('apellido'),
            "tipoIdentificacion": request.form.get('tipoIdentificacion'),
            "identificacion": request.form.get('identificacion'),
            "fechaNacimiento": fechaNacimiento,
            "direccion": request.form.get('direccion'),
            "telefono": request.form.get('telefono'),
            "email": request.form.get('email'),
            "sexo": request.form.get('sexo'),
        }
        print(data)

        r = requests.post(f"{URL_STATIC}/update", json=data, headers=headers)
        
        if r.status_code == 200:
            flash('Trabajador actualizado correctamente', 'success')
            return redirect(url_for('trabajador.home'))
        elif r.status_code == 400:
            error_data = r.json()  
            errors = error_data.get('errors', {})  
            sexo = get_options(f'{URL_STATIC}/sexo', headers)
            tipo = get_options(f'{URL_STATIC}/tipo_identificacion', headers)
            return render_template('trabajador_templates/edit_trabajador.html', trabajador=data, sexo=sexo, tipoIdentificacion=tipo, errors=errors)
        else:
            flash(f'Error desconocido: {r.status_code}', 'error')
            return redirect(url_for('trabajador.edit', id=id))

    except requests.exceptions.RequestException as e:
        flash(f'Error de conexión: {str(e)}', 'error')
        return redirect(url_for('trabajador.edit', id=id))        

@trabajador.route('/save', methods=['GET'])
def trabajador_save():
    token = session.get('token')
    if not token:
        flash('No tienes acceso. Inicia sesión primero.', 'error')
        return redirect(url_for('auth.login'))

    headers = {'Authorization': f'Bearer {token}'}
    sexo = get_options(f'{URL_STATIC}/sexo', headers)
    tipo = get_options(f'{URL_STATIC}/tipo_identificacion', headers)
    trabajador = {
        "nombre": "",
        "apellido": "",
        "tipoIdentificacion": "",
        "identificacion": "",
        "fechaNacimiento": "",
        "direccion": "",
        "telefono": "",
        "email": "",
        "sexo": ""
    }
    return render_template('trabajador_templates/registrar_trabajador.html', trabajador=trabajador, sexo=sexo, tipoIdentificacion=tipo)

@trabajador.route('/save', methods=['POST'])
def save():
    token = session.get('token')
    if not token:
        flash('No tienes acceso. Inicia sesión primero.', 'error')
        return redirect(url_for('auth.login'))

    headers = {'Authorization': f'Bearer {token}'}
    try:
        data = {
            "nombre": request.form.get('nombre'),
            "apellido": request.form.get('apellido'),
            "tipoIdentificacion": request.form.get('tipoIdentificacion'),
            "identificacion": request.form.get('identificacion'),
            "fechaNacimiento": request.form.get('fechaNacimiento'),
            "direccion": request.form.get('direccion'),
            "telefono": request.form.get('telefono'),
            "email": request.form.get('email'),
            "sexo": request.form.get('sexo'),
        }

        r = requests.post(f"{URL_STATIC}/save", json=data, headers=headers)
        
        if r.status_code == 200:
            flash('Trabajador guardado exitosamente.', 'success')
            return redirect(url_for('trabajador.home'))
        elif r.status_code == 400:
            error_data = r.json()  
            errors = error_data.get('errors', {})  
            for field, message in errors.items():
                flash(f'{field}: {message}', 'error')
            return render_template('trabajador_templates/registrar_trabajador.html', errors=errors, trabajador=data, sexo=get_options(f'{URL_STATIC}/sexo', headers), tipoIdentificacion=get_options(f'{URL_STATIC}/tipo_identificacion', headers))
        else:
            flash(f'Error desconocido: {r.status_code}', 'error')
            return redirect(url_for('trabajador.trabajador_save'))

    except requests.exceptions.RequestException as e:
        flash(f'Error de conexión: {str(e)}', 'error')
        return redirect(url_for('trabajador.trabajador_save'))

def get_options(endpoint, headers):
    try:
        r = requests.get(endpoint, headers=headers)
        r.raise_for_status()
        data = r.json()
        
        options = []
        for key, value in data.get("data", {}).items():
            nombre_formateado = format_string(value.replace("_", " "), capitalizar_palabras=True)
            options.append((key, nombre_formateado))
        
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