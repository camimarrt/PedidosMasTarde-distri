-- Base de datos de PedidosMasTarde.
--
-- 1. Crear la base (una sola vez, desde psql o pgAdmin):
--      CREATE DATABASE pedidosmastarde;
-- 2. Conectarse a la base pedidosmastarde y correr este archivo:
--      psql -U postgres -d pedidosmastarde -f schema.sql
--
-- NOTA: por ahora el programa todavia no usa estas tablas (en este avance
-- PedidosMasTarde solo consulta el stock de eBye). Se usaran cuando
-- implementemos Solicitar transporte, Cotizar envio y Cancelar/reprogramar envio.
DROP TABLE IF EXISTS envio CASCADE;
DROP TABLE IF EXISTS ruta;
DROP TABLE IF EXISTS conductor;

-- Un envio por cada solicitud de transporte que llega de eBye
CREATE TABLE envio (
    id SERIAL PRIMARY KEY,
    id_cliente INTEGER NOT NULL,
    nombre_destinatario VARCHAR(100),
	direccion VARCHAR(200),
    estado VARCHAR(50),
    id_ruta INTEGER,
	monto_total NUMERIC(12, 2)
);
