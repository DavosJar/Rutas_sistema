
from flask import Blueprint, abort, request, render_template, redirect, flash, jsonify, session, url_for
import requests
import json
import datetime

router = Blueprint('router', __name__)
URL_STATIC = 'http://localhost:8090/api'


@router.route('')
def home():
        if 'token' not in session:
            flash('Debes iniciar sesión primero.', 'error')
            return redirect(url_for('auth.login')) 
        num_conductores = get_num_items('conductor')
        num_vehiculos = get_num_items('vehiculo')
        num_pedidos = get_num_items('pedido')
        
        return render_template('index.html', num_conductores=num_conductores, num_vehiculos=num_vehiculos, num_pedidos=num_pedidos)  

def get_num_items(endpoint):
    r = requests.get(f"{URL_STATIC}/{endpoint}/list")
    try:
        data = r.json()
    except ValueError:
        flash(f"Error: La respuesta de la API no es un JSON válido. Respuesta: {r.text}", 'error')
        return 0
    return len(data.get('data', []))
