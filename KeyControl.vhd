library ieee;
use ieee.std_logic_1164.all;

ENTITY KeyControl IS
	PORT(
		clk_in, Kpress, Kack, CLEAR, CE: IN std_logic;
		Kval, Kscan: OUT std_logic
	);
END KeyControl;

ARCHITECTURE Behaviour OF KeyControl IS

	type STATE_TYPE is (STATE_SCANNING, STATE_READING, STATE_FINISHED);
		
	signal currState, nextState: STATE_TYPE;
BEGIN

currState <= STATE_SCANNING when CLEAR = '1' else nextState when rising_edge(clk_in);

generateNextState:
	process(Kpress, Kack, currState, nextState)
	begin
	  
	  case currState is
			when STATE_SCANNING => 	if(Kpress = '1') then 
												nextState <= STATE_READING;  
											end if;
			when STATE_READING => 	if(Kack = '1') then 
												nextState <= STATE_FINISHED; 
											end if;
			when STATE_FINISHED => 	if(Kack = '0' AND Kpress = '0') then
												nextState <= STATE_SCANNING;
											end if;
			when others => nextState <= STATE_READING;
	  end case;
	end process;  
	  
	Kscan <= '1' when (currState = STATE_SCANNING) else '0';
	Kval <= '1' when (currState = STATE_READING) else '0';
	  
	
	
END Behaviour;