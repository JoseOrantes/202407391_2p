"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express = require("express");
const { sequelize } = require("../../infrastructure/database/sequelize");
const { QueryTypes } = require("sequelize");

const router = express.Router();

// GET /api/censos (Con LEFT JOIN para obtener el estado dinámico)
router.get("/", async (req, res) => {
    try {
        const censos = await sequelize.query(
            `SELECT c.*, e.estado AS estado_nombre 
             FROM censos c 
             LEFT JOIN estado_censos e ON c.id_censo_estado = e.id_censo_estado`,
            { type: QueryTypes.SELECT }
        );
        res.json({ status: 200, body: censos });
    } catch (error) {
        res.status(500).json({ status: 500, message: error.message });
    }
});

// POST /api/censos
router.post("/", async (req, res) => {
    try {
        const { nombre, apellido, total_miembros, cantidad_televisiones, id_municipio, id_user, latitud, longitud, id_censo_estado } = req.body;
        
        // Obtener municipio existente en MariaDB
        const munis = await sequelize.query("SELECT id_municipio FROM municipios LIMIT 1", { type: QueryTypes.SELECT });
        const validMuniId = (munis && munis.length > 0) ? munis[0].id_municipio : (id_municipio || 1);

        const insertId = await sequelize.query(
            `INSERT INTO censos (nombre, apellido, total_miembros, cantidad_televisiones, id_municipio, id_user, latitud, longitud, id_censo_estado) 
             VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`,
            {
                replacements: [
                    nombre || '',
                    apellido || '',
                    total_miembros || 1,
                    cantidad_televisiones || 0,
                    validMuniId,
                    id_user || 1,
                    latitud || null,
                    longitud || null,
                    id_censo_estado || null
                ],
                type: QueryTypes.INSERT
            }
        );

        res.status(201).json({ standardResponse: { statusCode: 201, message: "Censo registrado exitosamente" }, body: { id_censo: insertId } });
    } catch (error) {
        console.error("Error al guardar censo:", error);
        res.status(500).json({ status: 500, message: error.message });
    }
});

exports.default = router;
