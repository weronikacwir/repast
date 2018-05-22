package tsp.relogo

import static repast.simphony.relogo.Utility.*
import static repast.simphony.relogo.UtilityG.*

import java.util.List

import repast.simphony.relogo.Plural
import repast.simphony.relogo.Stop
import repast.simphony.relogo.Utility
import repast.simphony.relogo.UtilityG
import repast.simphony.relogo.schedule.Go
import repast.simphony.relogo.schedule.Setup
import tsp.ReLogoTurtle

@Plural("Salesmen")
class Salesman extends ReLogoTurtle {
	
	List<City> route;
	double routeLength;
	
}