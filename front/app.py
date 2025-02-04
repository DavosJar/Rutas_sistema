from flask import Flask


def create_app():
    app = Flask(__name__, instance_relative_config=False)
    app.secret_key = "tu_secreto_flask"
    with app.app_context():
        from routes.route import router
        from routes.trabajador_route import trabajador
        from routes.punto_entrega_route import punto_entrega
        from routes.itinerario_route import itinerario
        from routes.orden_entrega_route import orden_entrega
        from routes.pedido_route import pedido
        from routes.auth_route import auth
        from routes.cliente_route import cliente
        from routes.vehiculo_route import vehiculo
        app.register_blueprint(router, url_prefix='/admin')
        app.register_blueprint(trabajador, url_prefix='/admin/trabajador')
        app.register_blueprint(punto_entrega, url_prefix='/admin/punto_entrega')
        app.register_blueprint(itinerario, url_prefix='/admin/itinerario')
        app.register_blueprint(orden_entrega, url_prefix='/admin/orden_entrega')
        app.register_blueprint(pedido, url_prefix='/admin/pedido')
        app.register_blueprint(auth, url_prefix='/')
        app.register_blueprint(cliente, url_prefix='/admin/cliente')
        app.register_blueprint(vehiculo, url_prefix='/admin/vehiculo')
        
    return app
