	package roboBLlejos;
import lejos.nxt.BasicMotorPort;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.comm.RConsole;
import lejos.robotics.Color;
public class junction {
	public final int left =0 ;
	public final int right =1;
	//int addPowConst=5;
	public int power = 60;
	
	int sleepTimer = 800; //in milliseconds
	int juncColor = Color.RED;
	int secNodeColor = Color.BLUE;
	public void takeRightTurn() throws InterruptedException{
		 lineFollower follow = new lineFollower();
		 follow.initMotors();
		// RConsole.println("TAKING RIGHT");
		 if(startProgram.debugRConsole==1)
			 RConsole.println("TAKING RIGHT");
		 else
		 {
			 startProgram.blconn.putData("TAKING RIGHT");
		 }
		 follow.leftMotor.controlMotor(power,BasicMotorPort.FORWARD);
		 follow.rightMotor.controlMotor(power,BasicMotorPort.FORWARD);
		 Thread.sleep(sleepTimer);
		 //RConsole.print(" TAKING RIGHT NOW , HAVE SLEPT ENOUGH");
		 compassHandler compassHandlerObject = new compassHandler();
		 
		 compassHandlerObject.changeDirection(right);
	}
	
	public void takeLeftTurn()throws InterruptedException{
		 lineFollower follow = new lineFollower();
		 
		 if(startProgram.debugRConsole==1)
			 RConsole.println("TAKING LEFT");
		 else
		 {
			 startProgram.blconn.putData("TAKING LEFT");
		 }
		 follow.initMotors();
		 follow.leftMotor.controlMotor(power,BasicMotorPort.FORWARD);
		 follow.rightMotor.controlMotor(power,BasicMotorPort.FORWARD);
		 Thread.sleep(sleepTimer);
		 if(startProgram.debugRConsole==1)
			 RConsole.println("TAKING LEFT , SLEPT ENOUGH NOW");
		 else
		 {
			 startProgram.blconn.putData("TAKING LEFT , SLEPT ENOUGH NOW");
		 }
		 
		 //RConsole.println("TAKING LEFT , SLEPT ENOUGH NOW");
			
		 compassHandler compassHandlerObject = new compassHandler();
		 
		 compassHandlerObject.changeDirection(left);
		
		 }
	
	public void fwdTillColor(int color, int color2){
	//	RConsole.print("FOLLOWING" );
		lineFollower follow = new lineFollower();

	 	if(startProgram.debugRConsole==1)
			RConsole.println(" FOLLOW TILL COLOR " +color2);
		 else
		 {
			 startProgram.blconn.putData(" FOLLOW TILL COLOR " +color2);
		 }
		
		if(color == Color.BLACK)
		{
			int lightValue =sensorHelp.getLightSensorReading();
			//RConsole.print("\n LIGHT Reading "+String.valueOf(lightValue));
			LCD.drawString(String.valueOf(lightValue),1,1);
			
			follow.initMotors();
			while(lightValue >= sensorHelp.lowLight+4 && sensorHelp.getColorSensorReading()!= Color.BLACK){
			//	RConsole.print("\n LIGHT Reading "+String.valueOf(lightValue));
				//RConsole.println("\n ----------------FOLLOWING TILL COLOR :------------"+ color);
				follow.moveMotorForward(follow.leftMotor, power);
				follow.moveMotorForward(follow.rightMotor, power);
				lightValue =sensorHelp.getLightSensorReading();
			}
		}
		else{
				int value =sensorHelp.getColorSensorReading();
				//RConsole.print("\n Reading "+String.valueOf(value));
				LCD.drawString(String.valueOf(value),1,1);
				follow.initMotors();
				while(value != color && value!=color2){
					//RConsole.println("\n ----------------FOLLOWING TILL COLOR :------------"+ color);
					follow.moveMotorForward(follow.leftMotor, power-2);
					follow.moveMotorForward(follow.rightMotor, power-2);
					value =sensorHelp.getColorSensorReading();
				}
				//stop motors
			}
		follow.leftMotor.controlMotor(0, BasicMotorPort.STOP);
		follow.rightMotor.controlMotor(0, BasicMotorPort.STOP);

	 	if(startProgram.debugRConsole==1)
			RConsole.println("FOLLOW TILL COLOR "+color + " COMPLETED");
		 else
		 {
			 startProgram.blconn.putData("FOLLOW TILL COLOR "+color + " COMPLETED");
		 }
		//RConsole.println();
		
	}
	
	public void handleJunction(String actionString, int nodeColor){
		char action= actionString.charAt(0);
		//LCD.drawString("TAKING ACTION",1,1);

		switch (action){
		case 'S':
			//forward till black line
			fwdTillColor(juncColor,juncColor);
			
			fwdTillColor(Color.BLACK,Color.BLACK);
			break;
		case 'U':
			fwdTillColor(juncColor,juncColor);
			//right turn
			try {
				takeRightTurn();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fwdTillColor(nodeColor,secNodeColor);

			fwdTillColor(juncColor,juncColor);
			//right turn
			try {
				takeRightTurn();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//forward till black line
			fwdTillColor(Color.BLACK,Color.BLACK);
			break;
		case 'L':
		 	if(startProgram.debugRConsole==1)
				RConsole.println("TOLD TO GO LEFT ");
			 else
			 {
				 startProgram.blconn.putData("TOLD TO GO LEFT ");
			 }
		//	RConsole.println();
			fwdTillColor(juncColor,juncColor);
			
			
			//left turn
			try {
				takeLeftTurn();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//forward till black line
			fwdTillColor(Color.BLACK,Color.BLACK);
			break;
		case 'R':
			if(startProgram.debugRConsole==1)
				RConsole.println("TOLD TO GO Right ");
			 else
			 {
				 startProgram.blconn.putData("TOLD TO GO Right ");
			 }
			

			fwdTillColor(juncColor,juncColor);  
			fwdTillColor(nodeColor,secNodeColor);
			fwdTillColor(juncColor,juncColor);
			
			//right turn
			try {
				takeRightTurn();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fwdTillColor(nodeColor,secNodeColor);
			
			fwdTillColor(juncColor,juncColor);

			 compassHandler compassHandlerObject = new compassHandler();
			 compassHandlerObject.alignRobot();
			 
			fwdTillColor(Color.BLACK,Color.BLACK);
			break;
		}
		if(startProgram.debugRConsole==1)
			RConsole.println(" JUNC HAND DONE ");
		 else
		 {
			 startProgram.blconn.putData(" JUNC HAND DONE");
		 }
		
	}
	
}
