from flask import Blueprint, render_template, request, redirect, url_for, flash, session
import requests

auth = Blueprint('auth', __name__)

API_LOGIN_URL = "http://localhost:8090/api/login"  
@auth.route('/', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        username = request.form.get('username')
        password = request.form.get('password')

        if not username or not password:
            flash('El nombre de usuario y la contraseña son requeridos', 'error')
            return redirect(url_for('auth.login'))
        response = requests.post(API_LOGIN_URL, json={"username": username, "password": password})
        
        if response.status_code == 200:
            data = response.json()
            if data.get('estado') == 'ok':
                flash('Inicio de sesión exitoso', 'success')
                session['token'] = data.get('token')
                return redirect(url_for('router.home'))  
            else:
                flash(data.get('data', 'Error en el inicio de sesión'), 'error')
        else:
            flash('Error al conectarse al servidor', 'error')

    return render_template('login/login.html')

@auth.route('/logout')
def logout():
    session.pop('token', None)
    flash('Sesión cerrada exitosamente', 'success')
    return redirect(url_for('auth.login'))


@auth.route('/dashboard')
def dashboard():
    token = session.get('token')
    if not token:
        flash('No tienes acceso. Inicia sesión primero.', 'error')
        return redirect(url_for('auth.login'))

    return render_template('auth/dashboard.html')