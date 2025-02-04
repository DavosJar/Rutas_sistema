from flask import Blueprint, render_template, request, redirect, url_for, flash, session, jsonify
import requests
import app

vehiculo = Blueprint('vehiculo', __name__)
URL_STATIC = 'http://localhost:8090/api/vehiculo'
app.secret_key = 'tu_clave_secreta_aqui'

def get_auth_headers():
    auth_token = session.get('token')
    if not auth_token:
        flash('Inicia sesión para continuar.', 'error')
        return None
    return {'Authorization': f'Bearer {auth_token}'}

@vehiculo.route('/<int:page>', methods=['GET'])
@vehiculo.route('/', methods=['GET'])
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
        vehiculos = data.get('data')
        total_items = data.get('totalItems', 0)
        items_per_page = data.get('itemsPerPage', 20)
        total_pages = (total_items // items_per_page) + (1 if total_items % items_per_page > 0 else 0)
        
        return render_template('/vehiculo_templates/vehiculo_list.html', vehiculos=vehiculos, page=page, total_pages=total_pages)
    else:
        flash('Error al obtener la lista de vehículos', 'error')
        return redirect(url_for('vehiculo.home'))

@vehiculo.route('/search/<attribute>/<value>', methods=['GET'])
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
        return render_template('/vehiculo_templates/vehiculo_list.html', vehiculos=data)
    else:
        flash('Error al buscar vehículos', 'error')
        return redirect(url_for('vehiculo.home'))

@vehiculo.route('/info/<id>', methods=['GET'])
def vehiculo_detail(id):
    token = session.get('token')
    if not token:
        flash('No tienes acceso. Inicia sesión primero.', 'error')
        return redirect(url_for('auth.login'))

    headers = {'Authorization': f'Bearer {token}'}
    try:
        r = requests.get(f"{URL_STATIC}/get/{id}", headers=headers)
        r.raise_for_status()
        data = r.json()
        vehiculo = data["data"]
        return render_template('/vehiculo_templates/vehiculo_detail.html', vehiculo=vehiculo)
    except requests.RequestException as e:
        return render_template('/error.html')

@vehiculo.route('/<id>/delete', methods=['GET'])
def delete(id):
    token = session.get('token')
    if not token:
        flash('No tienes acceso. Inicia sesión primero.', 'error')
        return redirect(url_for('auth.login'))

    headers = {'Authorization': f'Bearer {token}'}
    try:
        r = requests.delete(f"{URL_STATIC}/{id}/delete", headers=headers)
        r.raise_for_status()
        flash('Vehículo eliminado correctamente', 'success')
    except requests.RequestException as e:
        flash('Error al eliminar el vehículo', 'error')
    return redirect(url_for('vehiculo.home'))

@vehiculo.route('/order/<attribute>/<type>', methods=['GET'])
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
        return render_template('/vehiculo_templates/vehiculo_list.html', vehiculos=data)
    else:
        flash('Error al ordenar los vehículos', 'error')
        return redirect(url_for('vehiculo.home'))

@vehiculo.route('/<id>/edit', methods=['GET'])
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
        vehiculo = data["data"]

        estados = get_options(f'{URL_STATIC}/estados', headers)
        criterios = get_options(f'{URL_STATIC}/criterios', headers)

        return render_template('vehiculo_templates/edit_vehiculo.html', vehiculo=vehiculo, estados=estados, criterios=criterios, errors={})
    except requests.RequestException as e:
        flash('Error al obtener los datos del vehículo', 'error')
        return redirect(url_for('vehiculo.home'))

@vehiculo.route('/<id>/edit', methods=['POST'])
def update(id):
    token = session.get('token')
    if not token:
        flash('No tienes acceso. Inicia sesión primero.', 'error')
        return redirect(url_for('auth.login'))

    headers = {'Authorization': f'Bearer {token}'}
    try:

        data = {
            "id": int(id),
            "marca": request.form.get('marca'),
            "modelo": request.form.get('modelo'),
            "placa": request.form.get('placa'),
            "capacidad": float(request.form.get('capacidad')),
            "potencia": int(request.form.get('potencia')),
            "pesoTara": float(request.form.get('pesoTara')),
            "pesoMaximo": float(request.form.get('pesoMaximo')),
            "refrigerado": request.form.get('refrigerado'),
            "estado": request.form.get('estado')
        }

        print(data)

        r = requests.post(f"{URL_STATIC}/update", json=data, headers=headers)
        
        if r.status_code == 200:
            flash('Vehículo actualizado correctamente', 'success')
            return redirect(url_for('vehiculo.vehiculo_detail', id=id))
        elif r.status_code == 400:
            error_data = r.json()  
            errors = error_data.get('errors', {})  
            estados = get_options(f'{URL_STATIC}/estados', headers)
            criterios = get_options(f'{URL_STATIC}/criterios', headers)
            return render_template('vehiculo_templates/edit_vehiculo.html', vehiculo=data, estados=estados, criterios=criterios, errors=errors)
        else:
            flash(f'Error desconocido: {r.status_code}', 'error')
            return redirect(url_for('vehiculo.edit', id=id))

    except requests.exceptions.RequestException as e:
        flash(f'Error de conexión: {str(e)}', 'error')
        return redirect(url_for('vehiculo.edit', id=id))        

@vehiculo.route('/save', methods=['GET'])
def vehiculo_save():
    token = session.get('token')
    if not token:
        flash('No tienes acceso. Inicia sesión primero.', 'error')
        return redirect(url_for('auth.login'))

    headers = {'Authorization': f'Bearer {token}'}
    estados = get_options(f'{URL_STATIC}/estados', headers)
    criterios = get_options(f'{URL_STATIC}/criterios', headers)
    vehiculo = {
        "marca": "",
        "modelo": "",
        "placa": "",
        "capacidad": "",
        "potencia": "",
        "pesoTara": "",
        "pesoMaximo": "",
        "refrigerado": "",
        "estado": ""
    }
    return render_template('vehiculo_templates/registrar_vehiculo.html', vehiculo=vehiculo, estados=estados, criterios=criterios)

@vehiculo.route('/save', methods=['POST'])
def save():
    token = session.get('token')
    if not token:
        flash('No tienes acceso. Inicia sesión primero.', 'error')
        return redirect(url_for('auth.login'))

    headers = {'Authorization': f'Bearer {token}'}
    try:
        refrigerado = request.form.get('refrigerado')

        data = {
            "marca": request.form.get('marca'),
            "modelo": request.form.get('modelo'),
            "placa": request.form.get('placa'),
            "capacidad": float(request.form.get('capacidad')),
            "potencia": int(request.form.get('potencia')),
            "pesoTara": float(request.form.get('pesoTara')),
            "pesoMaximo": float(request.form.get('pesoMaximo')),
            "refrigerado": refrigerado,
            "estado": request.form.get('estado')
        }

        r = requests.post(f"{URL_STATIC}/save", json=data, headers=headers)
        print(data)
        if r.status_code == 200:
            flash('Vehículo guardado exitosamente.', 'success')
            return redirect(url_for('vehiculo.home'))
        elif r.status_code == 400:
            error_data = r.json()  
            errors = error_data.get('errors', {})  
            for field, message in errors.items():
                flash(f'{field}: {message}', 'error')
            return render_template('vehiculo_templates/registrar_vehiculo.html', errors=errors, vehiculo=data, estados=get_options(f'{URL_STATIC}/estados', headers), criterios=get_options(f'{URL_STATIC}/criterios', headers))
        else:
            flash(f'Error desconocido: {r.status_code}', 'error')
            return redirect(url_for('vehiculo.vehiculo_save'))

    except requests.exceptions.RequestException as e:
        flash(f'Error de conexión: {str(e)}', 'error')
        return redirect(url_for('vehiculo.vehiculo_save'))

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