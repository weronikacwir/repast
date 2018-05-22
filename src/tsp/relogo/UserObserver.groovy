package tsp.relogo

import static repast.simphony.relogo.Utility.*;
import static repast.simphony.relogo.UtilityG.*;

import java.util.List

import repast.simphony.relogo.Stop;
import repast.simphony.relogo.Utility;
import repast.simphony.relogo.UtilityG;
import repast.simphony.relogo.schedule.Go;
import repast.simphony.relogo.schedule.Setup;
import tsp.ReLogoObserver;

class UserObserver extends ReLogoObserver{

		@Setup
		def setup(){
			clearAll()
			
			ask(patches()) {
				setPcolor(white())
			}
			
			setDefaultShape(City, "circle")
			createCities(numCities) {
				setxy(randomXcor(), randomYcor())			
				setColor(red())	
				setSize(0.5)
			}
			
			createSalesmen(populationSize) {
				setSize(0)
				setColor(white())
				route = Graph.randomRoute(cities())
				routeLength = Graph.routeLength(route)
				hideTurtle()
			}
			shortestRouteLength = Double.MAX_VALUE;
		}
		
	
		@Go
		def go(){
			cleanup()
			updateBestRoute()
			evolveSalesmen()
		}

		def cleanup() {
			// in case some were produced for computations
			ask(dirtRoads()){
				die()
			}
		}
		

		def updateBestRoute() {
			Salesman winner = minOneOf(salesmen()) { routeLength };
			double minLength = winner.routeLength;
			
			if(minLength < shortestRouteLength) {
				shortestRouteLength = minLength;
				
				ask (roads()){
					die();
				}
				
				List<Road> shortestRoute = Graph.getRoads(winner.route);
				
				ask(shortestRoute) {
					setColor(blue())
					setWeight(3)
				}
			}
		}
			
		
		def evolveSalesmen () {
			
			List<Salesman> oldSalesmen = salesmen();
			
			// let the best salesmen survive till next generation
			int elite = Math.ceil(elitePortion * populationSize);
			if (elite % 2 == 1) {
				elite++;
			}
			
			List<Salesman> eliteSalesmen = minNOf(elite, salesmen()) {routeLength};
			
			for(int i = 0; i < elite; i++) {
				Salesman es = eliteSalesmen.get(i);
				createSalesmen(1) {
					setSize(0)
					setColor(white())
					route = es.route
					routeLength = es.routeLength
					hideTurtle()
				}
			}
			
			
			// create the rest of the new generation
			int numSalesmenToMake = populationSize - elite
			while (numSalesmenToMake > 0) {
				Salesman parent1 = selectParent(oldSalesmen)
				Salesman parent2 = selectParent(oldSalesmen)
				if (randomFloat(1) < crossoverProbability) {
					reproduceByCrossover(parent1, parent2)
				}
				else {
					reproduceByMutation(parent1)
					reproduceByMutation(parent2)
				}
				numSalesmenToMake = numSalesmenToMake - 2;
			}
			
			// kill the old generation
			ask(oldSalesmen) {
				die();
			}
		}
		
		Salesman selectParent(List<Salesman> oldSalesmen) {
			minOneOf(nOf(tournamentSize, oldSalesmen)) {routeLength};
		}
		
		def reproduceByCrossover(Salesman parent1, Salesman parent2) {
			List<City> route1 = parent1.route;
			List<City> route2 = parent2.route;
			
			// make child 1
			createSalesmen(1) {
				setSize(0)
				setColor(white())
				route = cross(route1, route2)
				routeLength = Graph.routeLength(route)
				hideTurtle()
			}
			
			// make child 2
			createSalesmen(1) {
				setSize(0)
				setColor(white())
				route = cross(route2, route1)
				routeLength = Graph.routeLength(route)
				hideTurtle()
			}
		}
		
		List<City> cross(List<City> routeA, List<City> routeB) {

			List<City> routeC = new ArrayList<>();
			int n = routeA.size();
			
			//start the new route with a random city
			City firstCity = oneOf(routeA)
			routeC.add(firstCity);
			
			City lastCity = firstCity;
			while(routeC.size() < n) {
				City nextCity = null;
				
				// consider neighbors of the last node added to routeC
				// in routeA and routeB as candidates for the next
				// city to add to routeC
				int posA = routeA.indexOf(lastCity)
				int posB = routeB.indexOf(lastCity)
				List<City> candidates = new ArrayList<>();
				candidates.add(routeA.get((posA - 1 + n) % n))
				candidates.add(routeA.get((posA + 1 + n) % n))
				candidates.add(routeB.get((posB - 1 + n) % n))
				candidates.add(routeB.get((posB + 1 + n) % n))
				
				// filter out candidates that are already in routeC
				candidates.removeAll(routeC)
				
				// pick the next city to go into routeC
				if (candidates.size() > 0) {
					nextCity = oneOf(candidates)
				}
				else {
					List<City> leftovers =  new ArrayList<>();
					leftovers.addAll(routeA);
					leftovers.removeAll(routeC)
					nextCity = oneOf(leftovers)
				}

				routeC.add(nextCity);
				lastCity = nextCity;
			}
			
			return routeC;
		}
		
		def reproduceByMutation(Salesman parent) {
			List<City> temp = new ArrayList<>()
			temp.addAll(parent.route);
			
			createSalesmen(1) {
				setSize(0)
				setColor(white())
				route = alterRoute(temp)
				routeLength = Graph.routeLength(it.route)
				hideTurtle()
			}
		}
		
		List<City> alterRoute(List<City> route) {
			List<City> temp = new ArrayList<>();
			temp.addAll(route);
			
			List<City> mutatedRoute = null;
			
			int decider = random(6);
			
			switch(decider) {
				case 0: mutatedRoute = Graph.swapRandom(temp);
						break;
				case 1: mutatedRoute = Graph.optimizeRandomCity(temp);
						break;
				case 2: mutatedRoute = Graph.replaceSubrouteWithGreedy(temp);
						break;
				case 3: mutatedRoute = Graph.untwist(temp);
						break;
				case 4: mutatedRoute = Graph.rotateRoute(temp);
					    break;
				default:mutatedRoute = Graph.reverseRandomSubroute(temp);
						break;
			}
			
			//println(decider + " " + mutatedRoute.size())
			return mutatedRoute;
		}
}