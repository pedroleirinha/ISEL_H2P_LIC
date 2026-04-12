library ieee;
use ieee.std_logic_1164.all;

ENTITY KeyTransmitterControl IS
	PORT(
		clk_in, Load, CLEAR, CE: IN std_logic;
		regEn, kbFree, startSignal, endSignal: OUT std_logic
	);
END KeyTransmitterControl;

ARCHITECTURE Behaviour OF KeyTransmitterControl IS

	type STATE_TYPE is (STATE_IDLE, STATE_LOADING, STATE_BEGIN_TRANSMISSION, 
		STATE_TRANSMITTING, STATE_END_TRANSMISSION);
		
	signal currState, nextState: STATE_TYPE;
BEGIN

	currState <= STATE_IDLE when CLEAR = '1' else nextState when rising_edge(clk_in);

generateNextState:
	process(Load, currState, nextState)
	begin
	  nextState <= currState;
	  
	  case currState is
			when STATE_IDLE 					=> 	if(Load = '1') then 
																nextState <= STATE_LOADING;  
															end if;
			when STATE_LOADING 				=> 	if(Load = '0') then 
																nextState <= STATE_BEGIN_TRANSMISSION;  
															end if;
															
			when STATE_BEGIN_TRANSMISSION =>	 	nextState <= STATE_TRANSMITTING;  
															
			when STATE_TRANSMITTING 		=> 	nextState <= STATE_END_TRANSMISSION;  
															
			when STATE_END_TRANSMISSION 	=> 	nextState <= STATE_IDLE;  
			
	  end case;
	
	
	end process;  
	  
	kbFree		<= '1' when  currState = STATE_IDLE else '0';
	startSignal <= '1' when  currState = STATE_BEGIN_TRANSMISSION else '0';
	endSignal 	<= '1' when  currState = STATE_END_TRANSMISSION else '0';
	regEn 		<= Load;

	
END Behaviour;