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

DROP TABLE IF EXISTS envio;
DROP TABLE IF EXISTS ruta;
DROP TABLE IF EXISTS conductor;

CREATE TABLE conductor (
    id          INTEGER PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    telefono    VARCHAR(20),
    disponible  BOOLEAN NOT NULL
);

-- Zonas de entrega con su costo y tiempo estimado (sirve para Cotizar envio)
CREATE TABLE ruta (
    id                INTEGER PRIMARY KEY,
    zona              VARCHAR(100) NOT NULL,
    costo             BIGINT NOT NULL,
    tiempo_estimado   VARCHAR(30) NOT NULL
);

-- Un envio por cada solicitud de transporte que llega de eBye
CREATE TABLE envio (
    id                    SERIAL PRIMARY KEY,
    id_cliente            INTEGER NOT NULL,
    nombre_destinatario   VARCHAR(100) NOT NULL,
    direccion             VARCHAR(200) NOT NULL,
    estado                VARCHAR(20) NOT NULL,
    id_ruta               INTEGER REFERENCES ruta(id),
    monto_total           BIGINT NOT NULL
);

INSERT INTO conductor (id, nombre, telefono, disponible) VALUES (1, 'Carlos Gomez', '981000111', TRUE);
INSERT INTO conductor (id, nombre, telefono, disponible) VALUES (2, 'Maria Lopez', '981000222', TRUE);

INSERT INTO ruta (id, zona, costo, tiempo_estimado) VALUES (1, 'Asuncion centro', 25000, '45 minutos');
INSERT INTO ruta (id, zona, costo, tiempo_estimado) VALUES (2, 'Gran Asuncion', 40000, '90 minutos');
