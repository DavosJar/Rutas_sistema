from flask import Blueprint, render_template, request, redirect, url_for, flash, session, jsonify
import requests

punto_entrega = Blueprint('punto_entrega', __name__)
URL_STATIC = 'http://localhost:8090/api/punto-entrega'

def get_auth_headers():
    auth_token = session.get('token')
    if not auth_token:
        flash('Inicia sesión para continuar.', 'error')
        return None
    return {'Authorization': f'Bearer {auth_token}'}

@punto_entrega.route('/<int:page>', methods=['GET'])
@punto_entrega.route('/', methods=['GET'])
def home(page=1):
    headers = get_auth_headers()
    if not headers:
        return redirect(url_for('auth.login'))

    r = requests.get(f"{URL_STATIC}/list/{page}", headers=headers)
    if r.status_code == 200:
        data = r.json()
        puntos_entrega = data.get('data')
        total_items = data.get('totalItems', 0)
        items_per_page = data.get('itemsPerPage', 20)
        total_pages = (total_items // items_per_page) + (1 if total_items % items_per_page > 0 else 0)
        
        return render_template('/punto_entrega_templates/punto_entrega_list.html', puntos_entrega=puntos_entrega, page=page, total_pages=total_pages)
    else:
        flash('Error al obtener la lista de puntos de entrega', 'error')
        return redirect(url_for('punto_entrega.home'))

@punto_entrega.route('/search/<attribute>/<value>/<int:page>', methods=['GET'])
def search(attribute, value, page=1):
    headers = get_auth_headers()
    if not headers:
        return redirect(url_for('auth.login'))

    r = requests.get(f"{URL_STATIC}/search/{attribute}/{value}/{page}", headers=headers)
    
    if r.status_code == 200:
        response = r.json()
        data = response.get('data', [])
        total_items = response.get('totalItems', 0)
        items_per_page = response.get('itemsPerPage', 20)
        total_pages = (total_items // items_per_page) + (1 if total_items % items_per_page > 0 else 0)
        
        if isinstance(data, dict):
            data = [data]
        
        return render_template('/punto_entrega_templates/punto_entrega_list.html', 
                               puntos_entrega=data, 
                               page=page, 
                               total_pages=total_pages,
                               attribute=attribute,
                               value=value)
    else:
        flash('Error al buscar puntos de entrega', 'error')
        return redirect(url_for('punto_entrega.home'))

@punto_entrega.route('/info/<id>', methods=['GET'])
def punto_entrega_detail(id):
    headers = get_auth_headers()
    if not headers:
        return redirect(url_for('auth.login'))

    try:
        r = requests.get(f"{URL_STATIC}/get/{id}", headers=headers)
        r.raise_for_status()
        data = r.json()
        punto_entrega = data["data"]
        return render_template('/punto_entrega_templates/punto_entrega_detail.html', punto_entrega=punto_entrega)
    except requests.RequestException as e:
        return render_template('/error.html')

@punto_entrega.route('/<id>/delete', methods=['GET'])
def delete(id):
    headers = get_auth_headers()
    if not headers:
        return redirect(url_for('auth.login'))

    try:
        r = requests.delete(f"{URL_STATIC}/{id}/delete", headers=headers)
        r.raise_for_status()
        flash('Punto de entrega eliminado correctamente', 'success')
    except requests.RequestException as e:
        flash('Error al eliminar el punto de entrega', 'error')
    return redirect(url_for('punto_entrega.home'))

@punto_entrega.route('/order/<attribute>/<type>', methods=['GET'])
def order(attribute, type):
    headers = get_auth_headers()
    if not headers:
        return redirect(url_for('auth.login'))

    r = requests.get(f"{URL_STATIC}/order/{attribute}/{type}/1", headers=headers)
    if r.status_code == 200:
        response = r.json()
        data = response.get('data', [])
        return render_template('/punto_entrega_templates/punto_entrega_list.html', puntos_entrega=data)
    else:
        flash('Error al ordenar los puntos de entrega', 'error')
        return redirect(url_for('punto_entrega.home'))

@punto_entrega.route('/<id>/edit', methods=['GET'])
def edit(id):
    headers = get_auth_headers()
    if not headers:
        return redirect(url_for('auth.login'))

    try:
        r = requests.get(f"{URL_STATIC}/get/{id}", headers=headers)
        r.raise_for_status()
        data = r.json()
        punto_entrega = data["data"]
        return render_template('punto_entrega_templates/edit_punto_entrega.html', punto_entrega=punto_entrega, errors={})
    except requests.RequestException as e:
        flash('Error al obtener los datos del punto de entrega', 'error')
        return redirect(url_for('punto_entrega.home'))

@punto_entrega.route('/<id>/edit', methods=['POST'])
def update(id):
    headers = get_auth_headers()
    if not headers:
        return redirect(url_for('auth.login'))

    try:
        data = {
            "id": int(id),
            "num": request.form.get('num'),
            "callePrincipal": request.form.get('callePrincipal'),
            "calleSecundaria": request.form.get('calleSecundaria'),
            "referencia": request.form.get('referencia')
        }

        r = requests.post(f"{URL_STATIC}/update", json=data, headers=headers)
        
        if r.status_code == 200:
            flash('Punto de entrega actualizado correctamente', 'success')
            return redirect(url_for('punto_entrega.punto_entrega_detail', id=id))
        elif r.status_code == 400:
            error_data = r.json()  
            errors = error_data.get('errors', {})  
            return render_template('punto_entrega_templates/edit_punto_entrega.html', punto_entrega=data, errors=errors)
        else:
            flash(f'Error desconocido: {r.status_code}', 'error')
            return redirect(url_for('punto_entrega.edit', id=id))

    except requests.exceptions.RequestException as e:
        flash(f'Error de conexión: {str(e)}', 'error')
        return redirect(url_for('punto_entrega.edit', id=id))        

@punto_entrega.route('/save', methods=['GET'])
def punto_entrega_save():
    headers = get_auth_headers()
    if not headers:
        return redirect(url_for('auth.login'))

    punto_entrega = {
        "num": "",
        "callePrincipal": "",
        "calleSecundaria": "",
        "referencia": ""
    }
    return render_template('punto_entrega_templates/registrar_punto_entrega.html', punto_entrega=punto_entrega)

@punto_entrega.route('/save', methods=['POST'])
def save():
    headers = get_auth_headers()
    if not headers:
        return redirect(url_for('auth.login'))

    try:
        data = {
            "num": request.form.get('num'),
            "callePrincipal": request.form.get('callePrincipal'),
            "calleSecundaria": request.form.get('calleSecundaria'),
            "referencia": request.form.get('referencia')
        }

        r = requests.post(f"{URL_STATIC}/save", json=data, headers=headers)
        if r.status_code == 200:
            flash('Punto de entrega guardado exitosamente.', 'success')
            return redirect(url_for('punto_entrega.home'))
        elif r.status_code == 400:
            error_data = r.json()  
            errors = error_data.get('errors', {})  
            for field, message in errors.items():
                flash(f'{field}: {message}', 'error')
            return render_template('punto_entrega_templates/registrar_punto_entrega.html', errors=errors, punto_entrega=data)
        else:
            flash(f'Error desconocido: {r.status_code}', 'error')
            return redirect(url_for('punto_entrega.punto_entrega_save'))

    except requests.exceptions.RequestException as e:
        flash(f'Error de conexión: {str(e)}', 'error')
        return redirect(url_for('punto_entrega.punto_entrega_save'))