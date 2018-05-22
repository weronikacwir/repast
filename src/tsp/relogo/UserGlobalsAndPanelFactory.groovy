package tsp.relogo

import repast.simphony.relogo.factories.AbstractReLogoGlobalsAndPanelFactory

public class UserGlobalsAndPanelFactory extends AbstractReLogoGlobalsAndPanelFactory{
	public void addGlobalsAndPanelComponents(){
		
		/**
		 * Place custom panels and globals below, for example:
		 * 
	        addGlobal("globalVariable1")	// Globally accessible variable ( variable name)
	        // Slider with label ( variable name, slider label, minimum value, increment, maximum value, initial value )
	        addSliderWL("sliderVariable", "Slider Variable", 0, 1, 10, 5)
	        // Slider without label ( variable name, minimum value, increment, maximum value, initial value )
	        addSlider("sliderVariable2", 0.2, 0.1, 0.8, 0.5)
	        // Chooser with label  ( variable name, chooser label, list of choices , zero-based index of initial value )
	        addChooserWL("chooserVariable", "Chooser Variable", ["yes","no","maybe"], 2)
	        // Chooser without label  ( variable name, list of choices , zero-based index of initial value )
	        addChooser("chooserVariable2", [1, 66, "seven"], 0)
	        // State change button (method name in observer)
	        addStateChangeButton("change")
	        // State change button with label (method name in observer, label)
	        addStateChangeButtonWL("changeSomething","Change Something")
	        
		 */

		addGlobal("shortestRouteLength")
		addGlobal("numCities")
		
		addSliderWL("numCities", "Number of Cities", 3, 1, 200, 30)
		addSliderWL("populationSize", "Population Size", 0, 2, 300, 100)
		addSliderWL("tournamentSize", "Tournament Size", 1, 1, 300, 7)
		addSliderWL("elitePortion", "Elite Portion", 0, 0.01, 1, 0.1)
		addSliderWL("crossoverProbability", "Probability of Crossover", 0, 0.01, 1, 0.5)
	}
	
}