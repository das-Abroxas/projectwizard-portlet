package life.qbic.projectwizard.adminviews;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Project;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.dto.Sample;
import life.qbic.datamodel.samples.ISampleBean;
import life.qbic.datamodel.samples.SampleSummary;
import life.qbic.datamodel.samples.SampleType;
import life.qbic.datamodel.samples.TSVSampleBean;
import life.qbic.expdesign.model.StructuredExperiment;
import life.qbic.openbis.openbisclient.IOpenBisClient;
import life.qbic.portal.samplegraph.GraphPage;

public class ServiceTestView extends VerticalLayout {

  private Button showGraph;
  private ComboBox projectBox;
  private GraphPage graph;
  private IOpenBisClient openbis;
  private Map<String, ISampleBean> idsToSamples;

  public ServiceTestView(IOpenBisClient openbis) {
    this.openbis = openbis;

    setSpacing(true);
    setMargin(true);

    List<String> projects = new ArrayList<>();
    for (Project p : openbis.listProjects()) {
      projects.add(p.getIdentifier());
    }

    projectBox = new ComboBox("Project");
    Collections.sort(projects);
    projectBox.addItems(projects);
    projectBox.setFilteringMode(FilteringMode.CONTAINS);
    showGraph = new Button("Update Graph");
    addComponent(projectBox);
    addComponent(showGraph);


    showGraph.addClickListener(new ClickListener() {
      @Override
      public void buttonClick(ClickEvent event) {
        if (graph != null) {
          removeComponent(graph);
        }
        graph = new GraphPage();
        addComponent(graph);
        if (projectBox.getValue() != null) {
          String project = (String) projectBox.getValue();
          List<Sample> samples = openbis.getSamplesOfProjectBySearchService(project);
          idsToSamples = new HashMap<>();
          for (Sample s : samples) {
            Map<String, String> props = s.getProperties();
            String sec = props.get("Q_SECONDARY_NAME");
            if (sec != null) {
              sec = "";
            }
            idsToSamples.put(s.getCode(),
                new TSVSampleBean(s.getCode(), s.getExperimentIdentifierOrNull(), project,
                    s.getSpaceCode(), SampleType.Q_TEST_SAMPLE, sec, new ArrayList<>(),
                    new HashMap<>()));
          }
          StructuredExperiment structuredExperiment = computeGraph(samples);
          System.out.println(structuredExperiment.getFactorsToSamples());
          System.out.println(idsToSamples);
          graph.setProjectGraph(structuredExperiment, idsToSamples);
        }
      }
    });
  }

  private StructuredExperiment computeGraph(List<Sample> samples) {
    Map<String, List<SampleSummary>> factorsToSamples = new HashMap<String, List<SampleSummary>>();
    Set<String> knownFactors = new HashSet<String>();
    knownFactors.add("Location");

    // init
    Map<String, Integer> idCounterPerLabel = new HashMap<String, Integer>();
    Map<String, Set<SampleSummary>> nodesForFactorPerLabel =
        new HashMap<String, Set<SampleSummary>>();
    for (String label : knownFactors) {
      idCounterPerLabel.put(label, 1);
      nodesForFactorPerLabel.put(label, new LinkedHashSet<SampleSummary>());
    }
    int id = 0;
    for (Sample s : samples) {
      id++;
      String type = s.getSampleTypeCode();
      if (type.equals(SampleType.Q_TEST_SAMPLE.toString())) {
        for (String label : knownFactors) {
          // compute new summary
          String location = "unknown";
          try {
            location = getLocation(s);
          } catch (Exception e) {
            System.out.println(e.getMessage()+" occured");
          }
          SampleSummary node = new SampleSummary(id, new HashSet<>(),
              new ArrayList<>(Arrays.asList(idsToSamples.get(s.getCode()))), location,
              tryShortenName(s.getProperties().get("Q_SAMPLE_TYPE"), s)+" "+location, type, true);

          // check for hashcode and add current sample s if node exists
          boolean exists = false;
          for (SampleSummary oldNode : nodesForFactorPerLabel.get(label)) {
            if (oldNode.equals(node)) {
              oldNode.addSample(idsToSamples.get(s.getCode()));
              exists = true;
              node = oldNode;
            }
          }
          if (!exists)
            idCounterPerLabel.put(label, idCounterPerLabel.get(label) + 1);
          // adds node if not already contained in set
          Set<SampleSummary> theseNodes = nodesForFactorPerLabel.get(label);
          theseNodes.add(node);
          nodesForFactorPerLabel.put(label, theseNodes);
        }
      }
    }
    for (String label : nodesForFactorPerLabel.keySet()) {
      Set<SampleSummary> nodes = nodesForFactorPerLabel.get(label);
      // addDataSetCount(nodes);
      factorsToSamples.put(label, new ArrayList<SampleSummary>(nodes));
    }
    return new StructuredExperiment(factorsToSamples);
  }


  private String getLocation(Sample s) throws JsonParseException, JsonMappingException, IllegalStateException, IOException {
     // base url of our service. maybe this should be in a config
     String baseURL = "http://services.qbic.uni-tuebingen.de:8080/sampletrackingservice/";
     // String baseURL = "http://localhost:8080/";
     HttpClient client = HttpClientBuilder.create().build();
     // define GET request using the respective endpoint
     HttpGet getContact = new HttpGet(baseURL + "samples/" + s.getCode());
     HttpResponse response = client.execute(getContact);
     // jackson databind uses data-model-lib classes to translate the http response object to this
     // class
     ObjectMapper mapper = new ObjectMapper();
     life.qbic.datamodel.services.Sample sample = mapper.readValue(response.getEntity().getContent(), life.qbic.datamodel.services.Sample.class);
     return sample.getCurrentLocation().getName();
  }

  private String shortenInfo(String info) {
    switch (info) {
      case "CARBOHYDRATES":
        return "Carbohydrates";
      case "SMALLMOLECULES":
        return "Smallmolecules";
      case "DNA":
        return "DNA";
      case "RNA":
        return "RNA";
      default:
        return WordUtils.capitalizeFully(info.replace("_", " "));
    }
  }

  private String tryShortenName(String key, Sample s) {
    switch (s.getSampleTypeCode()) {
      case "Q_BIOLOGICAL_ENTITY":
        return key;
      case "Q_BIOLOGICAL_SAMPLE":
        return key;
      case "Q_TEST_SAMPLE":
        String type = s.getProperties().get("Q_SAMPLE_TYPE");
        return key.replace(type, "") + " " + shortenInfo(type);
      case "Q_MHC_LIGAND_EXTRACT":
        return s.getProperties().get("Q_MHC_CLASS").replace("_", " ").replace("CLASS", "Class");
    }
    return key;
  }
}
