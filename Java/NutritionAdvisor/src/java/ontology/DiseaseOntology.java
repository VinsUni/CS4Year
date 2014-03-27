/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ontology;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import entity.DiseaseWS;
import entity.NutrientRestrictionWS;
import entity.NutrientsWS;
import entity.PersonWS;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import properties.PropertiesLoader;

/**
 *
 * @author Mada
 */
public final class DiseaseOntology {

    public static List<NutrientRestrictionWS> getNutrientsRestrictions(Model model, PersonWS person) {
        List<NutrientRestrictionWS> nutrientRestriction = new ArrayList<>();
 // long start=System.currentTimeMillis();
        if (person.getDiseases().size() > 0) {
            String queryString = "PREFIX foaf: <http://www.pips.eu.org/ontologies/food#>"
                    + "SELECT  ?minCarbohydrates ?maxCarbohydrates "
                    + "?minFats ?maxFats ?minSodium ?maxSodium "
                    + "WHERE { ?disease foaf:diseaseName ?diseaseName "
                    + "OPTIONAL{?disease foaf:minCarbohydrates ?minCarbohydrates} "
                    + "OPTIONAL{?disease foaf:maxCarbohydrates ?maxCarbohydrates} "
                    + "OPTIONAL{?disease foaf:minFats ?minFats} "
                    + "OPTIONAL{?disease foaf:maxFats ?maxFats} "
                    + "OPTIONAL{?disease foaf:minSodium ?minSodium} "
                    + "OPTIONAL{?disease foaf:maxSodium ?maxSodium} "
                    + "FILTER( " + getDiseasesFilterCondition(person.getDiseases()) + ")}";
            Query query = QueryFactory.create(queryString);
            QueryExecution qe = QueryExecutionFactory.create(query, model);
            ResultSet result = qe.execSelect();
        //long end = System.currentTimeMillis();
          //  System.out.println("time disease" + (end - start));
            if (result.hasNext()) {
                QuerySolution binding = result.nextSolution();
                nutrientRestriction = getNutrientsRestrictionsFromQuerySolution(binding);
            }
            qe.close();
        }
        return nutrientRestriction;
    }

    private static String getDiseasesFilterCondition(List<DiseaseWS> diseases) {
        String filterCondition = "";

        for (int diseaseIndex = 0; diseaseIndex < diseases.size() - 1; diseaseIndex++) {
            //string builder
            filterCondition = filterCondition + "?diseaseName= '" + diseases.get(diseaseIndex).getName() + "' || ";
        }
        filterCondition = filterCondition + "?diseaseName= '" + diseases.get(diseases.size() - 1).getName() + "'";

        //  System.out.println(filterCondition);
        return filterCondition;
    }

    private static List<NutrientRestrictionWS> getNutrientsRestrictionsFromQuerySolution(QuerySolution binding) {
        List<NutrientRestrictionWS> requieredNutrientsForUserDiseases = new ArrayList<>();

        Iterator<String> bindingVarIterator = binding.varNames();
        while (bindingVarIterator.hasNext()) {
            String bindingVar = bindingVarIterator.next();
            requieredNutrientsForUserDiseases.add(new NutrientRestrictionWS(bindingVar, binding.getLiteral(bindingVar).getDouble()));

        }
        return requieredNutrientsForUserDiseases;
    }

}