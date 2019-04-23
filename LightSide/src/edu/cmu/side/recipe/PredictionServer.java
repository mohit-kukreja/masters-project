/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.side.recipe;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.simpleframework.http.Part;
import org.simpleframework.http.Query;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import edu.cmu.side.Workbench;
import edu.cmu.side.control.ExtractFeaturesControl;
import edu.cmu.side.model.Recipe;
import edu.cmu.side.model.RecipeManager;
import edu.cmu.side.model.StatusUpdater;
import edu.cmu.side.model.data.DocumentList;
import edu.cmu.side.model.data.FeatureTable;
import edu.cmu.side.model.data.PredictionResult;
import edu.cmu.side.model.feature.Feature;
import edu.cmu.side.model.feature.Feature.Type;
import edu.cmu.side.model.feature.FeatureHit;
import edu.cmu.side.plugin.FeaturePlugin;
import edu.cmu.side.plugin.LearningPlugin;
import edu.cmu.side.plugin.ModelMetricPlugin;
import edu.cmu.side.plugin.SIDEPlugin;
import edu.cmu.side.plugin.WrapperPlugin;
import edu.cmu.side.plugin.control.PluginManager;
import edu.cmu.side.view.util.ParallelTaskUpdater;
import edu.cmu.side.view.util.RadioButtonListEntry;
import edu.cmu.side.view.util.SwingUpdaterLabel;
import plugins.features.BasicFeatures;
import weka.classifiers.bayes.NaiveBayes;
import plugins.learning.WekaBayes;
import edu.cmu.side.control.BuildModelControl;

import edu.cmu.side.model.data.TrainingResult;



/**
 * loads a model trained using lightSIDE uses it to label new instances via the
 * web. TODO (maybe): allow classification of multiple instances at once, or by
 * multiple classifiers, upload new trained models (possible?)
 * 
 * @author dadamson
 */
public class PredictionServer implements Container {
	protected static Map<String, Predictor> predictors = new HashMap<String, Predictor>();

	private final Executor executor;
	protected static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);


	public static void serve(int port, int threads) throws Exception {
		Container container = new PredictionServer(threads);

		Server server = new ContainerServer(container);
		Connection connection = new SocketConnection(server);
		SocketAddress address = new InetSocketAddress(port);

		connection.connect(address);
		System.out.println("Started server on port " + port + ".");
	}

	@Override
	public void handle(final Request request, final Response response) {
		executor.execute(new Runnable() {

			@Override
			public void run() {
				handleRequest(request, response);
			}

		});
	}

	public void handleRequest(Request request, Response response) {
		try {
			PrintStream body = response.getPrintStream();
			long time = System.currentTimeMillis();

			String target = request.getTarget();
			System.out.println(request.getMethod() + ": " + target);

			String answer = null;

			response.setValue("Content-Type", "text/plain");
			response.setValue("Server", "HelloWorld/1.0 (Simple 4.0)");
			response.setValue("Access-Control-Allow-Origin", "*");
			response.setDate("Date", time);
			response.setDate("Last-Modified", time);

			if (target.equals("/upload")) {
				if (request.getMethod().equals("POST")) {
					answer = handleUpload(request, response);
				} else {
					answer = handleGetUpload(request, response);
				}

			}

			else if (target.equals("/uploadinput")) {
				//System.out.println("here");
				if (request.getMethod().equals("POST")) {
					//System.out.println();
					answer = handleUploadInputDocument(request, response);
					if (answer=="Success")
					{
//						response.setDescription("Successfully uploaded file");
						answer= response.getDescription();
						response.setValue("file Uploaded",answer);
					}
						
				} else {
					answer = handleGetInputDocument(request, response);
				}
			}
			
			else if (target.equals("/uploadtest")) {
				//System.out.println("here");
				if (request.getMethod().equals("POST")) {
					//System.out.println();
					answer = handlePredictTest(request, response);
					if (answer=="Success")
					{
//						response.setDescription("Successfully uploaded file");
						answer= response.getDescription();
						response.setValue("file Uploaded",answer);
					}
						
				} else {
					answer = handleGetTestData(request, response);
				}
			}
			
//			else if (target.equals("/buildmodel")) {
//				System.out.println("buildmodel rest");
//				if (request.getMethod().equals("POST")) {
//					//System.out.println();
//					answer = handleBuildModel(request, response);	
//				} else {
//					answer = handleGetInputDocument(request, response);
//				}
//			}
			

			else if (target.startsWith("/try")) {
				if (request.getMethod().equals("POST")) {
					answer = handleEvaluate(request, response);
				} else {
					answer = handleGetEvaluate(request, response, "<h1>Try it out!</h1>");
				}
			}

			else if (target.startsWith("/predict")) {
				answer = handlePredict(request, response);
			} else if (target.startsWith("/favicon.ico")) {
				answer = handleIcon(request, response, body);
			}

			if (answer == null) {
				response.setCode(404);
				body.println("There is no data, only zuul.");
			} else
			{
				body.println(answer);
				//body.println(response.getDescription());
			}
				

			int code = response.getCode();
			if (code != 200) {
				body.println("HTTP Code " + code);
				//System.out.println("in ");
				System.out.println("HTTP Code " + code);
			}

			body.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String handleIcon(Request request, Response response, PrintStream out) {
		try {
			File icon = new File("toolkits/icons/bulbs/favicon.ico");
			response.setDate("Last-Modified", icon.lastModified());
			response.setValue("Content-Type", "image/ico");
			FileInputStream in = new FileInputStream(icon);

			byte[] buffer = new byte[1024];
			int len = in.read(buffer);
			while (len != -1) {
				out.write(buffer, 0, len);
				len = in.read(buffer);
				if (Thread.interrupted()) {
					throw new InterruptedException();
				}
			}
			return "";
		} catch (Exception e) {
			response.setCode(500);
			return "Ack!";
		}

	}

	protected String handleGetUpload(Request request, Response response) {
		response.setValue("Content-Type", "text/html");
		return "<head><title>Loader</title></head><body>" + "<h1>Loader</h1>"
				+ "<form action=\"upload\" method=\"post\" enctype=\"multipart/form-data\">"
				+ "Serialized Model: <input type=\"file\" name=\"model\"><br>"
				+ "Model Nickname:<input type=\"text\" name=\"modelNick\"> "
				+ "<input type=\"submit\" name=\"Submit\" value=\"Upload Model\">" + "</form>" + "</body>";
	}

	protected String handleGetInputDocument(Request request, Response response) {
		response.setValue("Content-Type", "text/html");
		return "<head><title>SIDE Loader</title></head><body>" + "<h1>Document Loader</h1>"
				+ "<form action=\"uploadinput\" method=\"post\" enctype=\"multipart/form-data\">"
				+ "Document File: <input type=\"file\" name=\"inputfile\"><br>"
				//+ "Document Nickname:<input type=\"text\" name=\"inputNick\"> "
				+ "<input type=\"submit\" name=\"Submit\" value=\"Upload File for Extraction\">" + "</form>" + "</body>";
	}
	
	protected String handleGetTestData(Request request, Response response) {
		response.setValue("Content-Type", "text/html");
		return "<head><title>SIDE Loader</title></head><body>" + "<h1>Upload Test Data</h1>"
				+ "<form action=\"uploadtest\" method=\"post\" enctype=\"multipart/form-data\">"
				+ "Document File: <input type=\"file\" name=\"inputfile\"><br>"
				//+ "Document Nickname:<input type=\"text\" name=\"inputNick\"> "
				+ "<input type=\"submit\" name=\"Submit\" value=\"Predict\">" + "</form>" + "</body>";
	}

	protected String handleGetEvaluate(Request request, Response response, String header) {
		org.simpleframework.http.Path path = request.getPath();
		if (path.getSegments().length > 1) {
			String modelName = request.getPath().getPath(1).substring(1);
			response.setValue("Content-Type", "text/html");
			return "<head><title>SIDE Effects</title></head><body style=\"margin:16px\">" + header
					+ "<form action=\"/try/" + modelName + "\" method=\"post\" enctype=\"multipart/form-data\">"
					+ "<label for=\"sample\">Test the " + modelName + " model:</label><br>"
					+ "<textarea rows=\"5\" cols=\"40\" name=\"sample\" style=\"max-width:400px\"></textarea><br>"
					+ "<input type=\"submit\" name=\"Submit\" value=\"Evaluate Text\">" + "</form>" + "</body>";
		} else {
			response.setCode(400);
			return "Must provide a model name: localhost:8000/try/modelname";
		}
	}

	protected String handleEvaluate(Request request, Response response) throws IOException, FileNotFoundException {
		Part part = request.getPart("sample");
		String sample = part.getContent();

		String modelName = request.getPath().getPath(1).substring(1);
		String answer = checkModel(response, modelName);
		if (!answer.equals("OK")) {
			return answer;
		}

		PredictionResult prediction = predictors.get(modelName).predict(new DocumentList(sample));

		Map<String, Double> scores = prediction.getDistributionMapForInstance(0);

		String header = "";
		for (String label : scores.keySet()) {
			header += String.format("%s: %.1f%%<br>", label, 100 * scores.get(label));
		}

		return handleGetEvaluate(request, response,
				"<h3>" + header + "</h3><p style=\"max-width:400px\"><i>" + sample + "</i></p>");
	}

	/**
	 * @param request
	 * @param body
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	protected String handleUploadInputDocument(Request request, Response response)throws IOException, FileNotFoundException {
		Part part = request.getPart("inputfile");
		String file_Name = part.getFileName();
		//copy the uploaded file into testdata folder
		final String destpath = Workbench.dataFolder.getAbsolutePath();
	    final Part filePart = request.getPart("inputfile");
	    final String filename = file_Name.substring(Math.max(file_Name.lastIndexOf("/"), file_Name.lastIndexOf("\\"))+1);
	   // System.out.print("filename:"+filename);
	    OutputStream out = null;
	    InputStream filecontent = null;
	    try {
	        out = new FileOutputStream(new File(destpath + File.separator
	                + filename));
	        filecontent = filePart.getInputStream();

	        int read = 0;
	        final byte[] bytes = new byte[1024];

	        while ((read = filecontent.read(bytes)) != -1) {
	            out.write(bytes, 0, read);
	        }
	    } catch (FileNotFoundException fne) {
	    	System.err.println("Error in prediction server");
	    } finally {
	        if (out != null) {
	            out.close();
	        }
	        if (filecontent != null) {
	            filecontent.close();
	        }
	    }
	    
	    Set<String> files = new HashSet<String>();
		files.add(file_Name);
		//creating a document list and setting all the required parameters for feature extraction
		DocumentList d = new DocumentList(files);
		String annot = "Vote";
		if (d.getTextColumns().contains(annot))
		{
			d.setTextColumn(annot, false);
		}
		
		Type valueType = d.getValueType(annot);

		Map<String, Boolean> columns = new TreeMap<String, Boolean>();
		for (String s : d.allAnnotations().keySet())
		{
			if (!annot.equals(s)) columns.put(s, false);
		}
		for (String s : d.getTextColumns())
		{
			columns.put(s, true);
		}
		
		//removing text column from all annotations and adding it to textcolumns 
		d.setTextColumn("text", true);
		Workbench.update(RecipeManager.Stage.DOCUMENT_LIST);
		
	    System.out.println("Completed process of load file");
	    
		RecipeManager rp=Workbench.getRecipeManager();
		Recipe plan=Workbench.recipeManager.fetchDocumentListRecipe(d);
		
		//adding an extractor to recipe i.e Basic Features
		FeaturePlugin b = new BasicFeatures();
		Collection<FeaturePlugin> plugins = new HashSet<FeaturePlugin>();
		plugins.add(b);
		
		Map<String, String> plugin_config = new HashMap<String, String>(); 
		plugin_config.put("Bigrams","false");
		plugin_config.put("Contains Non-Stopwords","false");
		plugin_config.put("Count Occurences","false");
		plugin_config.put("Ignore All-stopword N-Grams","false");
		plugin_config.put("Include Punctuation","true");
		plugin_config.put("Line Length","false");
		plugin_config.put("Normalize N-Gram Counts","false");
		plugin_config.put("POS Bigrams","false");
		plugin_config.put("POS Trigrams","false");
		plugin_config.put(" Skip Stopwords in N-Grams","false");
		plugin_config.put("Stem N-Grams","false");
		plugin_config.put("Track Feature Hit Location","true");
		plugin_config.put("Trigrams","false");
		plugin_config.put("Unigrams","true");
		plugin_config.put("Word/POS Pairs","false");
		plan.addExtractor(b, plugin_config);
		boolean halt=false;
		FeaturePlugin activeExtractor =  null;
		StatusUpdater update = new SwingUpdaterLabel();
	//checking the number of hits and generating feature table
		try
		{
			// System.out.println("EFC 289: extracting features for new feature table. Annotation "+selectedClassAnnotation+", type "+selectedClassType);
			Collection<FeatureHit> hits = new HashSet<FeatureHit>();
			for (SIDEPlugin plug : plan.getExtractors().keySet())
			{
				if (!halt)
				{  
					//	System.out.println("checking for added plugin: "+plan.getExtractors().get(plug).toString());
					activeExtractor = (FeaturePlugin) plug;
					//System.out.println("fetched active extractor"+activeExtractor.toString());
					hits.addAll(activeExtractor.extractFeatureHits(plan.getDocumentList(), plan.getExtractors().get(plug), update));
				}

			} 
			System.out.println("size of hits"+hits.size());
			if (!halt)
			{
				update.update("Building Feature Table");
				FeatureTable ft = new FeatureTable(plan.getDocumentList(), hits, 5 , "Vote" , Type.NOMINAL);
				ft.setName(file_Name+"testFeatures");
				plan.setFeatureTable(ft);
				
			} 
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, "Couldn't finish the feature table.\nSee lightside_log for more details.\n"+e.getLocalizedMessage(),"Feature Failure",JOptionPane.ERROR_MESSAGE);
			System.err.println("Feature Extraction Failed");
		}
	
		Collection<Feature> features=plan.getFeatureTable().getSortedFeatures();
		System.out.println("Number of features extracted:"+ features.size());
		for(Feature f: features )
		{
			System.out.println("feature name: "+f);
		}
		System.out.println("Created Feature Extraction!!");
		BuildModelControl.setHighlightedFeatureTableRecipe(plan);
		Workbench.getRecipeManager().addRecipe(plan);
		String s = handleBuildModel(plan);
		System.out.println(s);
		return "Success";
	}
	
	public String handleBuildModel(Recipe plan) {
	
		Map<LearningPlugin, Boolean> learningPlugins;
		SIDEPlugin[] learners = PluginManager.getSIDEPluginArrayByType("model_builder");
		
		plan=plan.addLearnerToRecipe(plan,(LearningPlugin)learners[2] , learners[2].generateConfigurationSettings());
		
		WekaBayes wb= new WekaBayes();
		plan.setLearnerSettings(wb.generateConfigurationSettings());
		
		BuildModelControl.updateValidationSetting("annotation", "Age");
		BuildModelControl.updateValidationSetting("foldMethod", "AUTO");
		BuildModelControl.updateValidationSetting("numFolds", "10");
		BuildModelControl.updateValidationSetting("source", "RANDOM");
		BuildModelControl.updateValidationSetting("test","true");
		BuildModelControl.updateValidationSetting("testRecipe", plan);
		BuildModelControl.updateValidationSetting("testSet", plan.getDocumentList());
		BuildModelControl.updateValidationSetting("type", "CV");
		
		
		try
		{
			FeatureTable current = plan.getTrainingTable();
			if (current != null)
			{
				TrainingResult results = null;
				if (results == null)
				{
					logger.info("Training new model.");
					results = plan.getLearner().train(current, plan.getLearnerSettings(), BuildModelControl.getValidationSettings(), plan.getWrappers(),
							BuildModelControl.getUpdater());
					
				}

				if (results != null)
				{
					System.out.println("Fetched Results successfully");
					plan.setTrainingResult(results);
					results.setName("BuiltNaiveBayes");

					plan.setLearnerSettings(plan.getLearner().generateConfigurationSettings());
					plan.setValidationSettings(new TreeMap<String, Serializable>(BuildModelControl.getValidationSettings()));
					System.out.println("confusion matrix key set"+results.getConfusionMatrix().keySet().size());
					System.out.println("Evaluation:"+results.getEvaluationTable().getSize());
					Map<String, String> allKeys = new TreeMap<String, String>();
						Collection<ModelMetricPlugin> plugins = BuildModelControl.getModelEvaluationPlugins();
						for(ModelMetricPlugin plugin : plugins){
							Map<String, String> evaluations = plugin.evaluateModel(results, plugin.generateConfigurationSettings());
							results.cacheEvaluations(evaluations);
							for(String s : evaluations.keySet()){
								Vector<Object> row = new Vector<Object>();
								row.add(s);
								try{
									Double d = Double.parseDouble(evaluations.get(s));
									row.add(d);
								}catch(Exception e){
									row.add(evaluations.get(s));
								}
							}
							allKeys.putAll(evaluations);
						}			
						
					System.out.println("Model Evaluation Matrix");
					for(String s:allKeys.keySet())
					{
						System.out.println(s+" "+allKeys.get(s));
					}
					
				}
			}
		}
		catch (Exception e)
		{
			
			plan = null;
			
		}
		Workbench.update(RecipeManager.Stage.TRAINED_MODEL);
		Workbench.getRecipeManager().addRecipe(plan);
		System.out.println("Recipe name:"+plan.getRecipeName());
		return "Success";
	}
	
	
	protected String handleUpload(Request request, Response response) throws IOException, FileNotFoundException {
		Part part = request.getPart("model");
		String nick = request.getPart("modelNick").getContent();
		String path = part.getFileName();
		// TODO: use threaded tasks.

		if (part != null && part.getFileName() != null) {
			if (path != null && !path.isEmpty() && (nick == null || nick.isEmpty())) {
				nick = path.replace(".model.side", "");
			}

			if (nick.isEmpty() || path.equals("null")) {
				response.setCode(400);
				return "upload requires both serialized model file and model name.";
			}
			nick = nick.replaceAll("/", "-");
			nick = nick.replaceAll("\\s", "-");

			if (predictors.containsKey(nick)) {
				response.setCode(409);
				return "nickname '" + nick + "'is already in use for a model at " + predictors.get(nick).getModelPath();
			}

			File f = new File("saved/" + nick + ".model.side");
			if (f.exists()) {
				response.setCode(409);
				return nick + " already exists on this server.";
			} else {
				// TODO: authentication?
				System.out.println(f.getAbsolutePath());
				FileChannel fo = new FileOutputStream(f).getChannel();
				ReadableByteChannel po = Channels.newChannel(part.getInputStream());
				long transferred = fo.transferFrom(po, 0, Integer.MAX_VALUE);
				System.out.println("wrote " + transferred + " bytes.");

				Long when = System.currentTimeMillis();
				boolean attached = attachModel(nick, f.getAbsolutePath());
				System.out.println("Attach model took " + (System.currentTimeMillis() - when) / 1000.0 + " seconds");

				if (attached)
					return "received " + path + ": " + transferred + " bytes.\nModel attached as /predict/" + nick + "";
				else {
					f.delete();
					response.setCode(418);
					return "could not attach model '" + path
							+ "' -- was it trained on the latest version of LightSide?";
				}
			}
		} else {
			response.setCode(400);
			return "No model file received.";
		}
	}

	public PredictionServer(int size) {
		this.executor = Executors.newFixedThreadPool(size);
	}
	
	protected String handlePredictTest(Request request, Response response) throws IOException {
		// TODO: use threaded tasks.
		String answer = "";
		Collection<Recipe> recipelist=Workbench.getRecipeManager().getRecipeCollectionByType(RecipeManager.Stage.TRAINED_MODEL);
		System.out.println("Fetched recipelist size:"+recipelist.size());
		
		List<Recipe> rp=new ArrayList<Recipe>(recipelist);
		Recipe trainedModel= rp.get(0);
		boolean useEvaluation=false;
		boolean showDists=false;
		boolean overwrite=false;
		DocumentList originalDocs;
		DocumentList newDocs = null;
		Exception ex = null;
		String name="PredictedTestData";
		try
		{
			/*if(useEvaluation)
			{
				originalDocs = trainedModel.getTrainingResult().getEvaluationTable().getDocumentList();

				TrainingResult results = trainedModel.getTrainingResult();
				List<String> predictions = (List<String>) results.getPredictions();
				newDocs = addLabelsToDocs(name, showDists, overwrite, originalDocs, results, predictions);
			}
			else
			{*/
				originalDocs = trainedModel.getDocumentList();

				Predictor predictor = new Predictor(trainedModel, name);
				newDocs = predictor.predict(originalDocs, name, showDists, overwrite);
				String[] a=newDocs.getAnnotationNames();
				
				for(String s:a)
				{
					System.out.println("column:"+s);
				}
					System.out.println("value:");
					List<String> l=newDocs.getAnnotationArray("PredictedTestData");
					for(String s1:l)
						System.out.print(s1+", ");
					
				
			//}
		}
		catch(Exception e)
		{
			ex = e;
			
		}
		if(newDocs.getSize()!=0)
		{
			answer="Success";
		}
		return answer;
	}

	protected String handlePredict(Request request, Response response) throws IOException {
		// TODO: use threaded tasks.
		String answer = "";
		String model = "";

		try {

			Query query = request.getQuery();

			List<String> instances = query.getAll("q");

			model = request.getPath().getPath(1).substring(1);

			Long when = System.currentTimeMillis();
			checkModel(response, model);
			System.out.println("Check model took " + (System.currentTimeMillis() - when) / 1000.0 + " seconds");

			System.out.println("using model " + model + " on " + instances.size() + " instances...");
			for (Comparable label : predictors.get(model).predict(instances)) {
				answer += label + " ";
			}
			answer = answer.trim();

			if (answer.isEmpty())
				response.setCode(500);

			return answer;

		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(400);
			return "could not handle request: " + request.getTarget()
					+ "\n(urls should be of the form /predict/model/?q=instance...)";
		}
	}

	protected String checkModel(Response response, String model) {
		if (!predictors.containsKey(model)) {
			// attempt to attach a local model
			File f = new File("saved/" + model + ".model.side");
			if (!f.exists())
				f = new File("saved/" + model + ".predict");
			if (!f.exists())
				f = new File("saved/" + model);

			if (f.exists()) {
				boolean attached = attachModel(model, f.getAbsolutePath());
				if (!attached) {
					response.setCode(418);
					return "could not load existing model for '" + model
							+ "' -- was it trained on the latest version of LightSide?";
				}
			} else {
				response.setCode(404);
				return "no model available named " + model;
			}
		}
		return "OK";
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			printUsage();
		}

		// initSIDE();
		int port = 8000;

		int start = 0;
		if (args.length > 0 && !args[0].contains(":")) {
			try {
				start = 1;
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				printUsage();
			}
		}

		for (int i = start; i < args.length; i++) {

			String[] modelConfig = args[i].split(":");
			String modelNick = modelConfig[0];
			String modelPath = modelConfig[1];

			attachModel(modelNick, modelPath);
		}

		if (predictors.isEmpty()) {
			System.out.println("Warning: no models attached yet. Use http://localhost:" + port + "/uploadinput");
		}

		serve(port, 5);

	}

	/**
	 * 
	 */
	protected static void printUsage() {
		System.out.println("usage: side_server.sh [port] model_nickname:path/to/model.side ...");
	}

	/**
	 * @param modelPath
	 * @param annotation
	 * @param annotation2
	 * @return
	 */
	protected static boolean attachModel(String nick, String modelPath) {
		try {
			System.out.println("attaching " + modelPath + " as " + nick);
			Predictor predict = new Predictor(modelPath, "class");
			predictors.put(nick, predict);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
