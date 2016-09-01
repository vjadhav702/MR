import java.util.ArrayList;
import java.util.Set;

public class cityProcessing {

	
	public static int Count_Seek_Inform_Neighbours_After_Robo_Ack = 0;
	
	public static class name_YesNoClass{
		String cityName;
		String yesNo;
		public name_YesNoClass() {
			// TODO Auto-generated constructor stub
		}
	}
	
	public static ArrayList<name_YesNoClass> ArrayList_Seek_Inform_Neighbours_After_Robo_Ack= null;
	
	public static void handleRequestForCity(String CityId)
	{
	//String nearestRobotName = getNearestRobotId(CityId);
		
		//startProgram.commClient.sendToServer(nearestRobotName+"@"+"MESSAGEFROMCITY:DEMANDFROM "+CityId);
		
	}
	

	
	public static String  createMessageForCity(String city, String message){
		return city + "@" + message;
	}
	
	/*
	 
	 
	 City1@REQUESTROBOT:City2:City2#40
City1@SEEKDEMAND:Robot0
City1@ACKNOWLEDGEMENT_ROBOT:Robot0:City2#YES
City1@SECACKNOWLEDGEMENT:City2:City2#YES



	 */
	public static void processMessage(String message)
	{
		
		System.out.println("Message Recv  \n" + message );
		
		try{
			
			
				String splitMessage[] = message.split(":");
				
				String headerType  =splitMessage[0];
				String messageFrom=splitMessage[1];
				
				// ACK # ROBO1#YES/NO
				System.out.println(splitMessage);
				
				if(headerType.equals(StringConst.STR_SEEK_DEMAND))
				{
				
				TellAllMyDemandtoRobo(messageFrom);	
				}
				
				
				if(headerType.equals(StringConst.STR_REQUEST))
						{
					
							String hashSplit[]= splitMessage[2].split("#");
							String demand = hashSplit[1];
							String goalId = hashSplit[0];
							
							System.out.print(" \nADDING DEMAND OF NEIGHBOUR TO LIST" + goalId + "  demand " + demand);
							TSPRequest tempObj = new TSPRequest(goalId,  Integer.parseInt(demand));
							startProgram.TSPRequestArrayList.add(tempObj);
							
							
						}
				
				
				if(headerType.equals(StringConst.STR_ACK_ROBOT))
				{
					Count_Seek_Inform_Neighbours_After_Robo_Ack=0;
					ArrayList_Seek_Inform_Neighbours_After_Robo_Ack = new ArrayList<cityProcessing.name_YesNoClass>();
					String messagePart = splitMessage[2];
					
					String acknowledgementsOfRequests[] =messagePart.split("%");
					
					
					
					for (int i =0 ; i< acknowledgementsOfRequests.length;i++)
					{
						String hashSplit[] = acknowledgementsOfRequests[i].split("#");
						
						String yesNo= hashSplit[1];
						String goalId=hashSplit[0];
						if(!(goalId.equals(startProgram.myCityId)) && yesNo.contains("YES"))
						{

							String messageSendonTel=goalId+"@"+StringConst.STR_INFO_FROM_CITY_ROBO_REACHED+":"+startProgram.myCityId+":"+messageFrom; // msgfrom is robo id
							sendMessageOnTelephone(messageSendonTel);	
							Count_Seek_Inform_Neighbours_After_Robo_Ack++;
							System.out.println(" \n SENDING TO Neighbours" + Count_Seek_Inform_Neighbours_After_Robo_Ack);
							
						}
						else
						{
							
								if(handleRequests.handlingReqViaSearchRobotORCity==-1) // robot case
								{
									if((goalId.equals(startProgram.myCityId)) && yesNo.equals("YES") )
									{
										String messageforhandler = messageFrom;
										handleRequests.messageFromOtherSystem_fromRobo=messageforhandler;
										
									
										handleRequests.shouldIwait_for_Robo=1;
									}
									
									else if((goalId.equals(startProgram.myCityId)) && yesNo.equals("NO"))
									{
										handleRequests.shouldIwait_for_Robo=-1;
						
									}
								}
								else if(handleRequests.handlingReqViaSearchRobotORCity==1)
									{
									
									
									// if robot says no to me
										if(goalId.equals(startProgram.myCityId) && yesNo.equals("NO"))
											break;
									
											// send sec ack for me to robot
									String payload = StringConst.STR_SECONDARYACK+":"+startProgram.myCityId+":" +startProgram.myCityId+"#" +"YES";
									String messageTo = messageFrom;
									
									sendMessageOnTelephone(messageTo+"@"+payload);
									
									// find my request
									for (int j=0 ; j<startProgram.TSPRequestArrayList.size();j++)
									{
										TSPRequest tempObj = startProgram.TSPRequestArrayList.get(j);
										if(tempObj.equals(startProgram.TSPRequestArrayList.get(j).getGoalCityId()))
										{
											startProgram.TSPRequestArrayList.get(j).setDemand(1);
											
											Set<String> neighBours= MapClass.getNeighbourNodes(startProgram.myCityId);
											ArrayList<String>neighBourArray  = new ArrayList<String>();
											neighBourArray.addAll(neighBours);	
											
											
											// send to all neighbours
											for(int k =0; k<neighBourArray.size();k++)
											{
												if(true)//!(neighBourArray.get(j).equals(messageFrom)))
												{
													String neighbour = neighBourArray.get(k);	
													
													String messageTel = neighbour+"@"+StringConst.STR_STATUS_CHANGE+":"+startProgram.myCityId+":"+startProgram.TSPRequestArrayList.get(k).getDemandStatus()+"#"+startProgram.TSPRequestArrayList.get(k).getDemandAckByRobot();
													sendMessageOnTelephone(messageTel);
													
													
												}
											
											}
											
										}
										
									}
									
									
									
									
									
									}
								
							
							
							
							
						}
						
					}
					
					
					
				}
				if(headerType.equals(StringConst.STR_REQUEST_FULLFILLED))
				{
					
					System.out.println(" REQ FULFLLED  By "+ messageFrom);
					for(int i =0; i <startProgram.TSPRequestArrayList.size();i++)
					{
						TSPRequest tempObj = startProgram.TSPRequestArrayList.get(i);
						if(tempObj.getGoalCityId().equals(startProgram.myCityId))
						{
							
							startProgram.TSPRequestArrayList.remove(i);
							// informs neighbours my demand is fulfilled

						}
					}
					
				}
					
					// header:sendercity:robofullfillingID
				if(headerType.equals(StringConst.STR_INFO_FROM_CITY_ROBO_REACHED))
				{
					String messagePart = splitMessage[2];
					String roboId = messagePart;
					
					// check status
					for(int i =0; i <startProgram.TSPRequestArrayList.size();i++)
					{
						TSPRequest tempObj = startProgram.TSPRequestArrayList.get(i);
						if(tempObj.getGoalCityId().equals(startProgram.myCityId) && tempObj.getDemandStatus()==0)
						{

							startProgram.TSPRequestArrayList.get(i).setDemandStatus(1);
							startProgram.TSPRequestArrayList.get(i).setDemandAckByRobot(roboId);
							
							//sec ack to roboId via city
							
							String messageToCity = messageFrom+"@"+StringConst.STR_SECONDARYACK+":"+ startProgram.myCityId+":"+roboId+"#"+"YES";
							sendMessageOnTelephone(messageToCity);
							
							// informs neighbours my demand is fulfilled
							
							Set<String> neighBours= MapClass.getNeighbourNodes(startProgram.myCityId);
							ArrayList<String>neighBourArray  = new ArrayList<String>();
							neighBourArray.addAll(neighBours);	
							
							for(int j =0; j <neighBourArray.size();j++)
							{
								if(true)//!(neighBourArray.get(j).equals(messageFrom)))
								{
									String neighbour = neighBourArray.get(j);	
									
									String messageTel = neighbour+"@"+StringConst.STR_STATUS_CHANGE+":"+startProgram.myCityId+":"+startProgram.TSPRequestArrayList.get(i).getDemandStatus()+"#"+startProgram.TSPRequestArrayList.get(i).getDemandAckByRobot();
									sendMessageOnTelephone(messageTel);
									
									
								}
							
							}
							
						}
						else if(tempObj.getGoalCityId().equals(startProgram.myCityId) && tempObj.getDemandStatus()==1)
						{

							String messageToCity = messageFrom+"@"+StringConst.STR_SECONDARYACK+":"+ startProgram.myCityId+":" +startProgram.myCityId+ "#" +"NO";
							sendMessageOnTelephone(messageToCity);
						}
						
					}
					
				}
				
				// header:senderCity:demandstatus#robot
				if(headerType.equals(StringConst.STR_STATUS_CHANGE))
				{
					
					
					String payload = splitMessage[2];
					
					String hashSplit[]= payload.split("#");
					String demandStatus = hashSplit[0];
					String robotHandling = hashSplit[1];
					
					for(int i =0; i <startProgram.TSPRequestArrayList.size();i++)
					{
						TSPRequest tempObj = startProgram.TSPRequestArrayList.get(i);

						if(tempObj.getGoalCityId().equals(messageFrom))
						{
							startProgram.TSPRequestArrayList.get(i).setDemandStatus(Integer.parseInt(demandStatus));
							startProgram.TSPRequestArrayList.get(i).setDemandAckByRobot(robotHandling);
							startProgram.TSPRequestArrayList.remove(i);
							
							
						}
						
					}
					
					
				}
				
				
				// sec ack from neighbour node , should be redireted to the concerned robot
				if(headerType.equals(StringConst.STR_SECONDARYACK))
				{
					
				
					String messageArray []= splitMessage[2].split("#");
					
					String yesNo = messageArray[1];
					String roboName = messageArray[0];
					Count_Seek_Inform_Neighbours_After_Robo_Ack--;
					if(Count_Seek_Inform_Neighbours_After_Robo_Ack>=0)
					{
						name_YesNoClass tempObj = new name_YesNoClass();
						tempObj.cityName= messageFrom;
						tempObj.yesNo=yesNo;
						ArrayList_Seek_Inform_Neighbours_After_Robo_Ack.add(tempObj);
					}
					if(Count_Seek_Inform_Neighbours_After_Robo_Ack==0)
					{
						// forward to robot
						
						String messageToRobot = roboName+"@"+ StringConst.STR_SECONDARYACK+":"+ startProgram.myCityId+":";
						
						for(int i =0; i <ArrayList_Seek_Inform_Neighbours_After_Robo_Ack.size();i++)
						{
							
							name_YesNoClass tempObj = ArrayList_Seek_Inform_Neighbours_After_Robo_Ack.get(i);
							String info = tempObj.cityName+"#"+ tempObj.yesNo;
													
							messageToRobot+= info;
							if(i!=ArrayList_Seek_Inform_Neighbours_After_Robo_Ack.size()-1)
							{
								messageToRobot+="%";
							}
						}
							
						sendMessageOnTelephone(messageToRobot);
						
					}
					
					
					
					
				}

				
		
				
				System.out.print("REQ HERE ***\n" );
				for (int i =0 ;i < startProgram.TSPRequestArrayList.size();i++)
				{
					TSPRequest tempObj = startProgram.TSPRequestArrayList.get(i);
					System.out.print("     " +tempObj.getGoalCityId()+ " : status " +  tempObj.demandStatus+"     ");
				}
				
				System.out.print("\n+++++++++++++++" );

		    }
				
				
		
					
		//			handleRequests.messageFromOtherSystem_fromRobo
					/*
					
					if(yesNo.equals("YES") && roboId.contains("Robot"))
					{
						String messageforhandler = roboId;
						handleRequests.messageFromOtherSystem_fromRobo=messageforhandler;
						
					
						handleRequests.shouldIwait_for_Robo=1;
					}
					
					else
					{
						handleRequests.shouldIwait_for_Robo=-1;
		
					}
					
				}
		}
		*/
				
				
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	public static void TellAllMyDemandtoRobo(String RoboName)
	{
		String payload="";
		String message= "";
		message+=RoboName+"@"+StringConst.STR_SEEK_DEMAND_REPLY+":" + startProgram.myCityId+":";
		
		for (int i=0; i< startProgram.TSPRequestArrayList.size();i++)
		{
			if(startProgram.TSPRequestArrayList.get(i).getDemandStatus()==0)
			{
				String goalId = startProgram.TSPRequestArrayList.get(i).getGoalCityId();
				int demand = startProgram.TSPRequestArrayList.get(i).getDemand();
				message+=goalId+"#"+demand;
				if(i!= startProgram.TSPRequestArrayList.size()-1)
				{
					message+="%";

				}
			
			}
	
		}
		sendMessageOnTelephone(message);
		
		
	}
	public static void sendMessageOnTelephone(String message)
	{
		
		startProgram.commClient.sendToServer(message);
		
	}
	
	

	
	
	
	
	
}
