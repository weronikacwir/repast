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
import tsp.ReLogoObserver

class Graph extends ReLogoObserver {
		
		static List<City> randomRoute(List<City> cities) {
			return shuffle(cities);
		}
		
		static double routeLength(List<City> route) {
			double routeLength = 0;
			
			City A = route.get(0);
			for (int i = 1; i < route.size(); i++) {
				City B = route.get(i);
				routeLength += A.distance(B);
				A = B;
			}
			routeLength += A.distance(route.get(0));
			
			return routeLength;
		}
		
		static List<Road> getRoads(List<City> route) {
			List<Road> roads = new ArrayList<>();
	
			City A = route.get(0);
			for (int i = 1; i < route.size(); i++) {
				City B = route.get(i);
				roads.add(A.createRoadWith(B) )
				A = B;
			}
			roads.add(A.createRoadWith(route.get(0)));
			
			return roads;
		}
		
		static List<DirtRoad> getDirtRoads(List<City> route) {
			List<DirtRoad> links = new ArrayList<>();
	
			City A = route.get(0);
			for (int i = 1; i < route.size(); i++) {
				City B = route.get(i);
				links.add(A.createDirtRoadWith(B))
				A = B;
			}
			links.add(A.createDirtRoadWith(route.get(0)));
			
			return links;
		}
		
		static List<City> swapRandom(List<City> route) {
			int posA = random(route.size());
			int posB = random(route.size());
			
			City A = route.get(posA);
			City B = route.get(posB);
			
			route.set(posA, B);
			route.set(posB, A);
			
			return route;
		}
		
		static List<City> reverseRandomSubroute(List<City> route) {
			int n = route.size();
			int i = random(n);
			int j = i + random(n - i);
			
			List<City> segment_0_i = route.subList(0, i);
			List<City> segment_i_j = route.subList(i, j);
			List<City> segment_j_n = route.subList(j, n);
			
			List<City> reversOfSeg_i_j = segment_i_j.reverse();
			
			List<City> newRoute = new ArrayList<>();
			newRoute.addAll(segment_0_i);
			newRoute.addAll(reversOfSeg_i_j);
			newRoute.addAll(segment_j_n);
			
			return newRoute;
		}
		
		static List<City> replaceSubrouteWithGreedy(List<City> route) {
			int n = route.size();
			if (n < 4) {
				return route;
			}
			
			int i = 1 + random(n-1);
			List<City> segment_0_i = route.subList(0, i);
			List<City> remainder = route.subList(i, n);
			
			List<City> newRoute = new ArrayList<>();
			newRoute.addAll(segment_0_i);
			
			City currentCity = newRoute.last();
			City closestCity = null;
			while (remainder.size() > 0) {
				int shortestDistance = Double.MAX_VALUE;
				int indexOfClosestCity
				for (int j = 0; j < remainder.size(); j++) {
					City otherCity = remainder.get(j);
					if (currentCity.distance(otherCity) < shortestDistance) {
						closestCity = otherCity;
						shortestDistance = currentCity.distance(closestCity);
						indexOfClosestCity = j;
					}
				}
				newRoute.add(closestCity);
				remainder.remove(indexOfClosestCity);
				currentCity = closestCity;
			}
			
			return newRoute;
		}
		
		static List<City> optimizeRandomCity(List<City> route) {
			
			City city = route.get(random(route.size()));
			List<City> otherCities = new ArrayList<>();
			otherCities.addAll(route);
			otherCities.remove(city);
			
			List<City> bestRouteYet = route;
			double bestLengthYet = routeLength(bestRouteYet);
			int n = otherCities.size();
			for (int i = 0; i <= n ; i++) {
				List<City> anotherRoute = new ArrayList<>();
				if (i > 0)
					anotherRoute.addAll(otherCities.subList(0, i));
				anotherRoute.add(city);
				if (i < n)
					anotherRoute.addAll(otherCities.subList(i, n));
				double anotherLength = routeLength(anotherRoute);
				if (anotherLength < bestLengthYet) {
					bestRouteYet = anotherRoute;
					bestLengthYet = anotherLength;
				}
			}
			if (bestRouteYet == routeLength(route)) {
				"can't optimize"
			}
			return bestRouteYet;
		}
		
		static List<City> untwist(List<City> route) {
			List<DirtRoad> links = getDirtRoads(route);
			List<DirtRoad> intersectingLinks = findIntersectingLinks(links);
			if(intersectingLinks != null) {
				List<Integer> indices = segmentToReverse(links, intersectingLinks);
				int n = route.size();
				int i = indices.get(0);
				int j = indices.get(1);
				List<City> newRoute = new ArrayList<>();
				newRoute.addAll(route.subList(0, i));
				newRoute.addAll(route.subList(i, j).reverse());
				newRoute.addAll(route.subList(j, n));
				return newRoute;
			}
			else {
				println("nothing to untwist")
				return route;
			}
		}
		
		static List<DirtRoad> findIntersectingLinks(List<DirtRoad> allLinks) {
			List crossingPairs = new ArrayList();
			int n = allLinks.size();
			for (int i=0; i < n - 1; i ++) {
				DirtRoad link1 = allLinks.get(i);
				for(int j=i+1; j < n; j++) {
					DirtRoad link2 = allLinks.get(j);
					if (intersect(link1, link2)) {
						crossingPairs.add(list(link1, link2));
					}
				}			
			}	
	
			List<DirtRoad> pairToUntwist = null;
			if (crossingPairs.size() > 0) {
				pairToUntwist = crossingPairs.get(random(crossingPairs.size()));
			}
			return pairToUntwist;
		}
		
		static boolean intersect(DirtRoad seg1, DirtRoad seg2) {
			City p1 = seg1.end1;
			City p2 = seg1.end2;
			City q1 = seg2.end1;
			City q2 = seg2.end2;
			
			return (removeDuplicates(list(p1, p2, q1, q2)).size() == 4) &&
				   (orientation(p1, p2, q1) != orientation(p1, p2, q2)) &&
				   (orientation(q1, q2, p1) != orientation(q1, q2, p2));	
		}
		
		static int orientation(City p, City q, City r) {
			double value = (q.ycor - p.ycor) * (r.xcor - q.xcor) -
						   (q.xcor - p.xcor) * (r.ycor - q.ycor);
			
			return (value < 0)? -1 : 1;
		}
		
		static List<Integer> segmentToReverse(List<DirtRoad> links, List<DirtRoad> intersectingLinks) {
			int p1 = 1 + links.indexOf(intersectingLinks.get(0));
			int p2 = 1 + links.indexOf(intersectingLinks.get(1));
			return list(p1, p2);
		}
		
		static List<City> rotateRoute(List<City> route) {
			List<City> newRoute = new ArrayList<>();
			
			int n = route.size();
			int pivot = random(n);
			
			for (int i = pivot; i < n; i++) {
				newRoute.add(route.get(i));
			}
			
			for (int i = 0; i < pivot; i++) {
				newRoute.add(route.get(i));
			}
			
			return newRoute;
		}
	

}
