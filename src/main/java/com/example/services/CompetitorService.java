/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Competitor;
import com.example.models.CompetitorDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Mauricio
 */
@Path("/competitors")
@Produces(MediaType.APPLICATION_JSON)
public class CompetitorService {

    @PersistenceContext(unitName = "CompetitorsPU")
    EntityManager entityManager;

    @PostConstruct
    public void init() {
        try {
            entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {

//        List<Competitor> competitors = new ArrayList<Competitor>();
//        Competitor competitorTmp = new Competitor("Carlos", "Alvarez", 35, "7658463", "3206574839 ", "carlos.alvarez@gmail.com", "Bogota", "Colombia", false);
//        Competitor competitorTmp2 = new Competitor("Gustavo", "Ruiz", 55, "2435231", "3101325467", "gustavo.ruiz@gmail.com", "Buenos Aires", "Argentina", false);
//        competitors.add(competitorTmp);
//        competitors.add(competitorTmp2);
        Query q = entityManager.createQuery("select u from Competitor u order by u.surname ASC");
        List<Competitor> competitors = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(competitors).build();
    }

    @POST
    @Path("/logIn")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logIn(String Json) throws JSONException {
        String correo = "";
        String contraseña = "";

        String[] login = Json.split("\"");
        correo = login[3];
        contraseña = login[7];
        System.out.println("correo: " + correo);
        System.out.println("contraesña: " + contraseña);
        Query q = entityManager.createQuery("select u from Competitor u order by u.surname ASC");
        List<Competitor> competitors = q.getResultList();
        JSONObject rta = new JSONObject();
        for (int i = 0; i < competitors.size(); i++) {
            System.out.println("asdfghjkl" + competitors.get(i).getContraseña());
            System.out.println("asdfghjkl" + competitors.get(i).getContraseña());
            if (contraseña.equals(competitors.get(i).getContraseña()) && correo.equals(competitors.get(i).getAddress())) {

                try {
                    rta.put("correo", competitors.get(i).getAddress());
                    rta.put("age", competitors.get(i).getAge());
                    rta.put("cellphone", competitors.get(i).getCellphone());
                    rta.put("City", competitors.get(i).getCity());
                    rta.put("Country", competitors.get(i).getCountry());
                    rta.put("Name", competitors.get(i).getName());
                    rta.put("Surname", competitors.get(i).getSurname());
                    rta.put("Telephone", competitors.get(i).getTelephone());
                } catch (JSONException ex) {
                    Logger.getLogger(CompetitorService.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            } else {
                rta.put("error:", "JAX-RS\n “NotAuthorizedException”.");
            }
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta).build();
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCompetitor(CompetitorDTO competitor) {
        JSONObject rta = new JSONObject();
        Competitor competitorTmp = new Competitor();
        competitorTmp.setAddress(competitor.getAddress());
        competitorTmp.setAge(competitor.getAge());
        competitorTmp.setCellphone(competitor.getCellphone());
        competitorTmp.setCity(competitor.getCity());
        competitorTmp.setCountry(competitor.getCountry());
        competitorTmp.setName(competitor.getName());
        competitorTmp.setSurname(competitor.getSurname());
        competitorTmp.setTelephone(competitor.getTelephone());
        competitorTmp.setContraseña(competitor.getContraseña());
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(competitorTmp);
            entityManager.getTransaction().commit();
            entityManager.refresh(competitorTmp);
            rta.put("competitor_id", competitorTmp.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            competitorTmp = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta).build();
    }

//    Competitor competitorTmp = new Competitor(competitor.getName(), competitor.getSurname(), competitor.getAge(), competitor.getTelephone(), competitor.getCellphone(), competitor.getAddress(), competitor.getCity(), competitor.getCountry(), false);
//
//    return Response.status (
//
//200).header("Access-Control-Allow-Origin", "*").entity(competitorTmp).build();
//    }
}
