package com.stockbetting.service.impl;

import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator;
import org.apache.spark.ml.feature.StandardScaler;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.tuning.CrossValidator;
import org.apache.spark.ml.tuning.CrossValidatorModel;
import org.apache.spark.ml.tuning.ParamGridBuilder;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stockbetting.dto.response.PredictionResponse;
import com.stockbetting.service.MLService;

import java.io.IOException;
import java.util.List;

/**
 * Advanced implementation of the {@link MLService} using Spark ML for stock price trend prediction.
 * This service trains a Logistic Regression model with hyperparameter tuning and performs cross-validation
 * for better accuracy.
 */
@Service
public class AdvancedMLService implements MLService {
    private static final String MODEL_PATH = "models/stock_prediction_model"; // Path to save/load the trained model
    private static final String OPEN_COLUMN = "Open"; // Column name for opening price
    private static final String HIGH_COLUMN = "High"; // Column name for highest price
    private static final String LOW_COLUMN = "Low"; // Column name for lowest price
    private static final String CLOSE_COLUMN = "Close"; // Column name for closing price
    private static final String VOLUME_COLUMN = "Volume"; // Column name for volume traded
    private static final String LABEL_COLUMN = "Label"; // Column for classification label

    private final SparkSession spark; // Spark session for processing data
    private CrossValidatorModel trainedModel; // The trained model (cross-validated)

    private static final Logger logger = LoggerFactory.getLogger(AdvancedMLService.class);

    /**
     * Initializes the service, setting up Spark session and loading/training the model.
     * 
     * @throws IOException If an error occurs while loading or training the model
     */
    public AdvancedMLService() throws IOException {
        this.spark = initializeSparkSession();
        this.trainedModel = loadOrTrainModel();
    }

    /**
     * Initializes a Spark session for data processing.
     * 
     * @return The initialized Spark session
     */
    private SparkSession initializeSparkSession() {
        return SparkSession.builder()
            .appName("StockBetting") // Set the application name
            .master("local[*]") // Run locally using all available cores
            .config("spark.memory.offHeap.enabled", true) // Enable off-heap memory for Spark
            .config("spark.memory.offHeap.size", "2g") // Allocate 2GB of off-heap memory for Spark
            .getOrCreate(); // Get or create the Spark session
    }

    /**
     * Loads the model if it exists or trains a new model if not found.
     * 
     * @return The trained model
     * @throws IOException If model loading or training fails
     */
    private CrossValidatorModel loadOrTrainModel() throws IOException {
        try {
            // Try to load the existing model from the specified path
            return CrossValidatorModel.load(MODEL_PATH);
        } catch (Exception e) {
            // If the model doesn't exist, train a new one
            logger.info("Model not found, training new model");
            return trainNewModel();
        }
    }

    /**
     * Trains a new machine learning model using stock market data.
     * 
     * @return The trained cross-validator model
     * @throws IOException If training or data loading fails
     */
    private CrossValidatorModel trainNewModel() throws IOException {
        // Define the schema for the training data
        var schema = createSchema();
        // Load the training data from CSV file
        var data = loadTrainingData(schema);
        // Create the machine learning pipeline
        var pipeline = createPipeline();
        // Define the parameter grid for hyperparameter tuning
        var paramGrid = createParamGrid();
        
        // Train and evaluate the model using cross-validation
        return trainModel(data, pipeline, paramGrid);
    }

    /**
     * Creates the schema for the stock data.
     * 
     * @return A schema defining the structure of the stock data
     */
    private StructType createSchema() {
        return DataTypes.createStructType(List.of(
            DataTypes.createStructField(OPEN_COLUMN, DataTypes.DoubleType, false),
            DataTypes.createStructField(HIGH_COLUMN, DataTypes.DoubleType, false),
            DataTypes.createStructField(LOW_COLUMN, DataTypes.DoubleType, false),
            DataTypes.createStructField(CLOSE_COLUMN, DataTypes.DoubleType, false),
            DataTypes.createStructField(VOLUME_COLUMN, DataTypes.DoubleType, false),
            DataTypes.createStructField(LABEL_COLUMN, DataTypes.DoubleType, false)
        ).toArray(new StructField[0]));
    }

    /**
     * Loads the stock market training data from a CSV file.
     * 
     * @param schema The schema to be used for loading the data
     * @return The loaded dataset
     */
    private Dataset<Row> loadTrainingData(StructType schema) {
        return spark.read()
            .option("header", true) // Read CSV with headers
            .schema(schema) // Use the provided schema
            .csv("data/stock_data.csv"); // Path to the training data
    }

    /**
     * Creates the machine learning pipeline consisting of feature assembly, scaling, and logistic regression.
     * 
     * @return The configured machine learning pipeline
     */
    private Pipeline createPipeline() {
        var assembler = new VectorAssembler()
            .setInputCols(new String[]{OPEN_COLUMN, HIGH_COLUMN, LOW_COLUMN, CLOSE_COLUMN, VOLUME_COLUMN}) // Input features
            .setOutputCol("features"); // Output column for assembled features

        var scaler = new StandardScaler()
            .setInputCol("features") // Scale the assembled features
            .setOutputCol("scaledFeatures"); // Output column for scaled features

        var lr = new LogisticRegression()
            .setFeaturesCol("scaledFeatures") // Use scaled features for the logistic regression model
            .setLabelCol(LABEL_COLUMN); // Set the label column

        return new Pipeline().setStages(new PipelineStage[]{assembler, scaler, lr});
    }

    /**
     * Creates a parameter grid for tuning hyperparameters during model training.
     * 
     * @return The parameter grid for cross-validation
     */
    private ParamGridBuilder createParamGrid() {
        return new ParamGridBuilder()
            .addGrid(new LogisticRegression().regParam(), new double[]{0.01, 0.1, 1.0}) // Regularization parameter
            .addGrid(new LogisticRegression().elasticNetParam(), new double[]{0.0, 0.5, 1.0}); // ElasticNet parameter
    }

    /**
     * Trains the machine learning model using the specified data, pipeline, and parameter grid.
     * 
     * @param data The training dataset
     * @param pipeline The machine learning pipeline
     * @param paramGrid The parameter grid for hyperparameter tuning
     * @return The trained model
     * @throws IOException If training fails
     */
    private CrossValidatorModel trainModel(Dataset<Row> data, Pipeline pipeline, ParamGridBuilder paramGrid) throws IOException {
        var cv = new CrossValidator()
            .setEstimator(pipeline) // Set the pipeline as the estimator
            .setEvaluator(new BinaryClassificationEvaluator().setLabelCol(LABEL_COLUMN)) // Set the evaluator for binary classification
            .setEstimatorParamMaps(paramGrid.build()) // Set the parameter grid for cross-validation
            .setNumFolds(5); // Perform 5-fold cross-validation

        // Split data into training and test sets
        var splits = data.randomSplit(new double[]{0.8, 0.2}, 42);
        // Train the model using the training set
        var model = cv.fit(splits[0]);
        
        // Evaluate the model using the test set
        evaluateModel(model, splits[1]);
        // Save the trained model to disk
        model.save(MODEL_PATH);
        
        return model;
    }

    /**
     * Evaluates the trained model using the test dataset.
     * 
     * @param model The trained model
     * @param testData The test dataset
     */
    private void evaluateModel(CrossValidatorModel model, Dataset<Row> testData) {
        var predictions = model.transform(testData); // Get model predictions
        var evaluator = new BinaryClassificationEvaluator().setLabelCol(LABEL_COLUMN); // Create evaluator for binary classification
        var accuracy = evaluator.evaluate(predictions); // Calculate accuracy
        
        logger.info("Model Accuracy: {:.2f}%", accuracy * 100); // Log model accuracy
    }

    /**
     * Makes a prediction for a given stock symbol based on the provided stock data.
     * 
     * @param symbol The stock symbol (e.g., "AAPL")
     * @param open The opening price
     * @param high The highest price
     * @param low The lowest price
     * @param close The closing price
     * @param volume The trading volume
     * @return The prediction response containing prediction result and probability
     */
    @Override
    public PredictionResponse predict(String symbol, double open, double high, 
                                      double low, double close, long volume) {
        // Create a dataset for the given input
        var input = createPredictionInput(open, high, low, close, volume);
        // Make the prediction using the trained model
        var prediction = trainedModel.transform(input);
        
        // Create and return the prediction response
        return createPredictionResponse(prediction);
    }

    /**
     * Creates a dataset for prediction input using the provided stock data.
     * 
     * @param open The opening price
     * @param high The highest price
     * @param low The lowest price
     * @param close The closing price
     * @param volume The trading volume
     * @return The dataset for prediction
     */
    private Dataset<Row> createPredictionInput(double open, double high, 
                                               double low, double close, long volume) {
        return spark.createDataFrame(
            List.of(RowFactory.create((Object[]) new Double[]{open, high, low, close, (double) volume})),
            createInputSchema()
        );
    }

    /**
     * Creates the schema for the prediction input dataset.
     * 
     * @return The schema for the prediction input
     */
    private StructType createInputSchema() {
        return DataTypes.createStructType(List.of(
            DataTypes.createStructField(OPEN_COLUMN, DataTypes.DoubleType, false),
            DataTypes.createStructField(HIGH_COLUMN, DataTypes.DoubleType, false),
            DataTypes.createStructField(LOW_COLUMN, DataTypes.DoubleType, false),
            DataTypes.createStructField(CLOSE_COLUMN, DataTypes.DoubleType, false),
            DataTypes.createStructField(VOLUME_COLUMN, DataTypes.DoubleType, false)
        ).toArray(new StructField[0]));
    }

    /**
     * Creates a prediction response containing the result and probability.
     * 
     * @param prediction The prediction dataset
     * @return The prediction response with result and probability
     */
    private PredictionResponse createPredictionResponse(Dataset<Row> prediction) {
        var result = prediction.select("prediction").first().getDouble(0);
        var probability = prediction.select("probability").first().getDouble(0);
        
        return new PredictionResponse(
            result > 0.5 ? "UP" : "DOWN", // Prediction result ("UP" or "DOWN")
            result > 0.5 ? "BUY" : "SELL", // Recommendation ("BUY" or "SELL")
            probability, // Prediction probability
            "Prediction based on historical patterns" // Explanation
        );
    }
}
