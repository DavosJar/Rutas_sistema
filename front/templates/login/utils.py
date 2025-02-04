from flask import session, redirect, url_for, request, flash
import requests
from functools import wraps

# Decorador para verificar el token
def token_required(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        token = session.get('token')  # Obtenemos el token de la sesión
        if not token:
            flash('Inicia sesión para continuar.', 'error')
            return redirect(url_for('auth.login'))  # Redirige al login si no hay token

        # Validar el token con el backend
        try:
            headers = {"Authorization": f"Bearer {token}"}
            response = requests.get('http://localhost:8090/validate', headers=headers)  # Ajusta la URL
            if response.status_code != 200:
                raise Exception("Token inválido o expirado")
        except Exception as e:
            session.pop('token', None)  # Elimina el token de la sesión si falla
            flash('Tu sesión ha expirado. Por favor, inicia sesión nuevamente.', 'error')
            return redirect(url_for('auth.login'))
        
        return f(*args, **kwargs)
    return decorated_function
