from flask import Blueprint, render_template, request, redirect, url_for, flash, session
import requests

cliente = Blueprint('cliente', __name__)

URL_STATIC = 'http://localhost:8090/api/cliente'

@cliente.route('/', methods=['GET'])
def home():
    try:
        r = requests.get(f"{URL_STATIC}/list")
        print(f"Status Code: {r.status_code}")
        print(f"Response Content: {r.text}")
        r.raise_for_status()
        data = r.json()
        clientes = data.get('data', [])
        return render_template('/cliente_templates/cliente_list.html', clientes=clientes)
    except requests.RequestException as e:
        flash('Error al obtener la lista de clientes', 'error')
        return redirect(url_for('cliente.home'))

@cliente.route('/<int:id>', methods=['GET'])
def cliente_detail(id):
    try:
        r = requests.get(f"{URL_STATIC}/get/{id}")
        r.raise_for_status()
        data = r.json()
        cliente = data.get('data', {})
        return render_template('/cliente_templates/cliente_detail.html', cliente=cliente)
    except requests.RequestException as e:
        flash('Error al obtener los detalles del cliente', 'error')
        return redirect(url_for('cliente.home'))

@cliente.route('/save', methods=['GET'])
def save_form():
    return render_template('/cliente_templates/registrar_cliente.html')

@cliente.route('/save', methods=['POST'])
def save():
    try:
        data = {
            "razonSocial": request.form.get('razonSocial'),
            "ruc": request.form.get('ruc'),
            "telefono": request.form.get('telefono'),
            "email": request.form.get('email')
        }
        print(data)
        r = requests.post(f"{URL_STATIC}/save", json=data)
        r.raise_for_status()
        flash('Cliente guardado correctamente', 'success')
    except requests.RequestException as e:
        flash('Error al guardar el cliente', 'error')
    return redirect(url_for('cliente.home'))

@cliente.route('/<id>/delete', methods=['GET'])
def delete(id):
    token = session.get('token')
    if not token:
        flash('No tienes acceso. Inicia sesión primero.', 'error')
        return redirect(url_for('auth.login'))
    headers = {'Authorization': f'Bearer {token}'}
    try:
        r = requests.delete(f"{URL_STATIC}/{id}/delete", headers=headers)
        r.raise_for_status()
        flash('Cliente eliminado correctamente', 'success')
    except requests.RequestException as e:
        flash('Error al eliminar el cliente', 'error')
    return redirect(url_for('cliente.home'))

@cliente.route('/<int:id>/edit', methods=['GET'])
def edit(id):
    try:
        r = requests.get(f"{URL_STATIC}/get/{id}")
        r.raise_for_status()
        data = r.json()
        cliente = data.get('data', {})
        return render_template('/cliente_templates/edit_cliente.html', cliente=cliente)
    except requests.RequestException as e:
        flash('Error al obtener los datos del cliente', 'error')
        return redirect(url_for('cliente.home'))

@cliente.route('/<int:id>/edit', methods=['POST'])
def update(id):
    try:
        data = {
            "id": id,
            "razonSocial": request.form.get('razonSocial'),
            "ruc": request.form.get('ruc'),
            "telefono": request.form.get('telefono'),
            "email": request.form.get('email')
        }
        r = requests.post(f"{URL_STATIC}/update", json=data)
        r.raise_for_status()
        flash('Cliente actualizado correctamente', 'success')
    except requests.RequestException as e:
        flash('Error al actualizar el cliente', 'error')
    return redirect(url_for('cliente.home'))

@cliente.route('/search/<attribute>/<value>', methods=['GET'])
def search(attribute, value):
    try:
        r = requests.get(f"{URL_STATIC}/list/search/{attribute}/{value}")
        r.raise_for_status()
        response = r.json()
        data = response.get('data', [])
        if isinstance(data, dict):
            data = [data]
        return render_template('/cliente_templates/cliente_list.html', clientes=data)
    except requests.RequestException as e:
        flash('Error al buscar clientes', 'error')
        return redirect(url_for('cliente.home'))

@cliente.route('/order/<attribute>/<int:order>', methods=['GET'])
def order(attribute, order):
    try:
        r = requests.get(f"{URL_STATIC}/list/order/{attribute}/{order}")
        r.raise_for_status()
        response = r.json()
        data = response.get('data', [])
        return render_template('/cliente_templates/cliente_list.html', clientes=data)
    except requests.RequestException as e:
        flash('Error al ordenar los clientes', 'error')
        return redirect(url_for('cliente.home'))