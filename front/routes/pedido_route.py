from flask import Blueprint, render_template, request, redirect, url_for, flash, session
import requests
from datetime import datetime
import app

pedido = Blueprint('pedido', __name__)
URL_STATIC = 'http://localhost:8090/api/pedido'
app.secret_key = 'tu_clave_secreta_aqui'

def get_auth_headers():
    auth_token = session.get('token')
    if not auth_token:
        flash('Inicia sesión para continuar.', 'error')
        return None
    return {'Authorization': f'Bearer {auth_token}'}

def handle_request_error(e, message):
    flash(f'{message}: {e}', 'error')
    return redirect(url_for('pedido.home'))

def get_options(endpoint, headers):
    try:
        r = requests.get(endpoint, headers=headers)
        r.raise_for_status()
        data = r.json()
        options = []
        for item in data.get("data", []):
            nombre_formateado = format_string(item.replace("_", " "), capitalizar_palabras=True)
            options.append((item, nombre_formateado))
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

@pedido.route('/', methods=['GET'])
def home():
    headers = get_auth_headers()
    if not headers:
        return redirect(url_for('auth.login'))

    try:
        r = requests.get(f"{URL_STATIC}/list", headers=headers)
        r.raise_for_status()
        data = r.json()
        pedidos = data.get('data', [])
        return render_template('/pedido_templates/pedido_list.html', pedido=pedidos)
    except requests.RequestException as e:
        return handle_request_error(e, 'Error al obtener la lista de pedidos')

@pedido.route('/search/<attribute>/<value>', methods=['GET'])
def search(attribute, value):
    headers = get_auth_headers()
    if not headers:
        return redirect(url_for('auth.login'))

    try:
        r = requests.get(f"{URL_STATIC}/list/search/{attribute}/{value}", headers=headers)
        r.raise_for_status()
        response = r.json()
        data = response.get('data', [])
        if isinstance(data, dict):
            data = [data]
        return render_template('/pedido_templates/pedido_list.html', pedido=data)
    except requests.RequestException as e:
        return handle_request_error(e, 'Error al buscar pedidos')

@pedido.route('/info/<id>', methods=['GET'])
def pedido_detail(id):
    headers = get_auth_headers()
    if not headers:
        return redirect(url_for('auth.login'))

    try:
        r = requests.get(f"{URL_STATIC}/get/{id}", headers=headers)
        r.raise_for_status()
        data = r.json()
        pedido = data["data"]
        return render_template('/pedido_templates/pedido_detail.html', pedido=pedido)
    except requests.RequestException as e:
        return handle_request_error(e, 'Error al obtener detalles del pedido')
    
@pedido.route('/<id>/delete', methods=['GET'])
def delete(id):
    headers = get_auth_headers()
    if not headers:
        return redirect(url_for('auth.login'))

    try:
        r = requests.delete(f"{URL_STATIC}/{id}/delete", headers=headers)
        r.raise_for_status()
        flash('Pedido eliminado correctamente', 'success')
    except requests.RequestException as e:
        return handle_request_error(e, 'Error al eliminar el pedido')
    return redirect(url_for('pedido.home'))

@pedido.route('/order/<attribute>/<type>', methods=['GET'])
def order(attribute, type):
    headers = get_auth_headers()
    if not headers:
        return redirect(url_for('auth.login'))

    try:
        r = requests.get(f"{URL_STATIC}/order/{attribute}/{type}", headers=headers)
        r.raise_for_status()
        response = r.json()
        data = response.get('data', [])
        return render_template('/pedido_templates/pedido_list.html', pedido=data)
    except requests.RequestException as e:
        return handle_request_error(e, 'Error al ordenar los pedidos')

@pedido.route('/<id>/edit', methods=['GET'])
def edit(id):
    headers = get_auth_headers()
    if not headers:
        return redirect(url_for('auth.login'))

    try:
        r = requests.get(f"{URL_STATIC}/get/{id}", headers=headers)
        r.raise_for_status()
        data = r.json()
        pedido = data["data"]

        if pedido.get("fechaRegistro"):
            try:
                pedido["fechaRegistro"] = datetime.strptime(pedido["fechaRegistro"], "%Y-%m-%dT%H:%M:%S.%fZ").strftime("%Y-%m-%d")
            except ValueError:
                pedido["fechaRegistro"] = datetime.strptime(pedido["fechaRegistro"], "%Y-%m-%d").strftime("%Y-%m-%d")

        contenido = get_options(f'{URL_STATIC}/listType', headers)
        return render_template('/pedido_templates/edit_pedido.html', pedido=pedido, contenido=contenido)
    except requests.RequestException as e:
        return handle_request_error(e, 'Error al obtener datos para editar el pedido')

@pedido.route('/edit', methods=['POST'])
def update():
    headers = get_auth_headers()
    if not headers:
        return redirect(url_for('auth.login'))

    try:
        data = {
            "id": int(request.form.get('id')),
            "contenido": request.form.get('contenido'),
            "fechaRegistro": request.form.get('registro'),
            "requiereFrio": request.form.get('refrigeracion'),
            "volumenTotal": float(request.form.get('volumen')),
            "pesoTotal": float(request.form.get('peso')),
            "idPuntoEntrega": int(request.form.get('puntoEntrega')),
            "idCliente": int(request.form.get('cliente')),
        }
        r = requests.post(f"{URL_STATIC}/update", json=data, headers=headers)
        r.raise_for_status()
        return redirect(url_for('pedido.home'))
    except requests.RequestException as e:
        flash(f"Error al actualizar el pedido: {e}", "danger")
        return redirect(url_for('pedido.edit', id=id))

@pedido.route('/save', methods=['GET'])
def save():
    headers = get_auth_headers()
    if not headers:
        return redirect(url_for('auth.login'))

    contenido = get_options(f'{URL_STATIC}/listType', headers)
    
    try:
        r = requests.get('http://localhost:8090/api/cliente/list', headers=headers)
        r.raise_for_status()
        data = r.json()
        clientes = data.get("data", [])
    except requests.RequestException as e:
        print(f'Error al obtener clientes: {e}')
        clientes = []

    try:
        puntos_entrega = []
        page = 1
        while True:
            r = requests.get(f'http://localhost:8090/api/punto-entrega/list/{page}', headers=headers)
            r.raise_for_status()
            data = r.json()
            puntos_entrega.extend(data.get("data", []))
            
            total_items = data.get('totalItems', 0)
            items_per_page = data.get('itemsPerPage', 20)
            total_pages = (total_items // items_per_page) + (1 if total_items % items_per_page > 0 else 0)
            
            if page >= total_pages:
                break
            page += 1

    except requests.RequestException as e:
        print(f'Error al obtener puntos de entrega: {e}')
        puntos_entrega = []

    return render_template('/pedido_templates/registrar_pedido.html', 
                         contenido=contenido, 
                         clientes=clientes, 
                         puntos_entrega=puntos_entrega)

@pedido.route('/save', methods=['POST'])
def save_post():
    headers = get_auth_headers()
    if not headers:
        return redirect(url_for('auth.login'))

    try:
        data = {
            "contenido": request.form.get('contenido'),
            "fechaRegistro": request.form.get('registro'),
            "requiereFrio": request.form.get('refrigeracion') == "true",
            "volumenTotal": float(request.form.get('volumen')),
            "pesoTotal": float(request.form.get('peso')),
            "idPuntoEntrega": int(request.form.get('puntoEntrega')),
            "idCliente": int(request.form.get('cliente')),
        }
        r = requests.post(f"{URL_STATIC}/save", json=data, headers=headers)
        r.raise_for_status()
        return redirect(url_for('pedido.home'))
    except requests.RequestException as e:
        flash(f"Error al guardar el pedido: {e}", "danger")
        return redirect(url_for('pedido.save'))