from flask import Blueprint, render_template, request, redirect, url_for, flash, session
import requests

conductor = Blueprint('conductor', __name__)

URL_STATIC = 'http://localhost:8090/api/conductor'


@conductor.route('/', methods=['GET'])
def home():
    try:
        r = requests.get(f"{URL_STATIC}/list")
        print(f"Status Code: {r.status_code}")
        print(f"Response Content: {r.text}")
        r.raise_for_status()
        data = r.json()
        conductores = data.get('data', [])
        return render_template('/conductor_template/conductores_list.html', conductores=conductores)
    except requests.RequestException as e:
        flash('Error al obtener la lista de conductores', 'error')
        return redirect(url_for('conductor.home'))
@conductor.route('/get/<int:id>', methods=['GET'])
def conductor_detail(id):
    try:
        r = requests.get(f"{URL_STATIC}/{id}")
        r.raise_for_status()
        data = r.json()
        conductor = data.get('data', {})
        return render_template('/conductor_template/conductor_detail.html', conductor=conductor)
    except requests.RequestException as e:
        flash('Error al obtener los detalles del conductor', 'error')
        return redirect(url_for('conductor.home'))


@conductor.route('/save', methods=['GET', 'POST'])
def save_conductor():
    if request.method == 'POST':
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
                "licenciaConducir": request.form.get('licenciaConducir'),
                "caducidadLicencia": request.form.get('caducidadLicencia'),
                "salario": request.form.get('salario'),
                "turno": request.form.get('turno'),
                "estado": request.form.get('estado')
            }

            r = requests.post(f"{URL_STATIC}/save", json=data)
            r.raise_for_status()
            flash('Conductor guardado con éxito', 'success')
            return redirect(url_for('conductor.home'))
        except requests.RequestException as e:
            flash('Error al guardar el conductor: ' + str(e), 'error')
            return redirect(url_for('conductor.save_conductor'))
    return render_template('/conductor_template/conductor_save.html')

@conductor.route('/edit/<int:id>', methods=['GET', 'POST'])
def edit_conductor(id):
    if request.method == 'POST':
        try:
            data = {
                "id": id,
                "nombre": request.form.get('nombre'),
                "apellido": request.form.get('apellido'),
                "tipoIdentificacion": request.form.get('tipoIdentificacion'),
                "identificacion": request.form.get('identificacion'),
                "fechaNacimiento": request.form.get('fechaNacimiento'),
                "direccion": request.form.get('direccion'),
                "telefono": request.form.get('telefono'),
                "email": request.form.get('email'),
                "sexo": request.form.get('sexo'),
                "licenciaConducir": request.form.get('licenciaConducir'),
                "caducidadLicencia": request.form.get('caducidadLicencia'),
                "salario": request.form.get('salario'),
                "turno": request.form.get('turno'),
                "estado": request.form.get('estado')
            }

            r = requests.post(f"{URL_STATIC}/update", json=data)
            r.raise_for_status()
            flash('Conductor actualizado con éxito', 'success')
            return redirect(url_for('conductor.home'))
        except requests.RequestException as e:
            flash('Error al actualizar el conductor: ' + str(e), 'error')
            return redirect(url_for('conductor.edit_conductor', id=id))

    try:

        r = requests.get(f"{URL_STATIC}/{id}")
        r.raise_for_status()
        data = r.json()
        conductor = data.get('data', {})
        return render_template('/conductor_template/conductor_edit.html', conductor=conductor)
    except requests.RequestException as e:
        flash('Error al obtener los detalles del conductor', 'error')
        return redirect(url_for('conductor.home'))


@conductor.route('/delete/<int:id>', methods=['GET'])
def delete_conductor(id):
    try:

        r = requests.delete(f"{URL_STATIC}/{id}/delete")
        r.raise_for_status()
        flash('Conductor eliminado con éxito', 'success')
    except requests.RequestException as e:
        flash('Error al eliminar el conductor: ' + str(e), 'error')
    return redirect(url_for('conductor.home'))