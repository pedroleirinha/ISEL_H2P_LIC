library ieee;
use ieee.std_logic_1164.all;

ENTITY RingBufferControl IS
	PORT(
		clk_in, DAV, CTS, full, empty, CLEAR: 	IN std_logic;
		Wreg, DAC, Wr, selPG, incPut, incGet:	OUT std_logic
	);
END RingBufferControl;

ARCHITECTURE Behaviour OF RingBufferControl IS
	
	type STATE_TYPE is (STATE_IDLE, 
		STATE_READ_START, STATE_READ_RAM, STATE_READ_INC, 
		STATE_WRITE_START, STATE_WRITE_RAM, STATE_WRITE_END, STATE_WRITE_INC 
	);
		
	signal currState, nextState: STATE_TYPE;
	
BEGIN

	currState <= STATE_IDLE when CLEAR = '1' else nextState when rising_edge(clk_in);
	
	Wreg		<= '1' when  currState = STATE_READ_RAM else '0'; -- flag que permite escrever no key transmitter a word tirada da RAM
	Wr			<= '1' when  currState = STATE_WRITE_RAM else '0';-- flag que permite escrever uma nova entradad na RAM
	
--	DAC 		<= '1' when  (currState = STATE_WRITE_END) or (currState = STATE_IDLE and full = '1')  else '0';-- Sinaliza que a word foi escrita na RAM com sucesso
	DAC 		<= '1' when  (currState = STATE_WRITE_END) else '0';-- Sinaliza que a word foi escrita na RAM com sucesso

	
	selPG		<= '1' when  currState = STATE_WRITE_START OR 
								 currState = STATE_WRITE_RAM   OR 
								 currState = STATE_WRITE_END   else '0';
	
	incPut	<= '1' when  currState = STATE_WRITE_INC else '0';
	
	incGet	<= '1' when  currState = STATE_READ_INC else '0';
	

generateNextState:
	process(DAV, full, empty, CTS, currState)
	begin
	
	  nextState <= currState;
	  
	  case currState is
			when STATE_IDLE 				=> 	if(DAV = '1') then
			
															if(full = '0') then
																nextState <= STATE_WRITE_START;
																
															elsif (CTS = '1') then 
																	nextState <= STATE_READ_START;
																else
																	nextState <= STATE_IDLE;
															end if;
															
															
														elsif (CTS = '0') then
																    nextState <= STATE_IDLE;  															
															elsif (empty = '1') then
																	nextState <= STATE_IDLE; 
																else 
																	nextState <= STATE_READ_START;
														end if;
															
			when STATE_WRITE_START		=> 	nextState <= STATE_WRITE_RAM;  
			when STATE_WRITE_RAM			=> 	nextState <= STATE_WRITE_END;
			when STATE_WRITE_END		 	=> 	if(DAV = '0') then 
															nextState <= STATE_WRITE_INC;
														else
															nextState <= STATE_WRITE_END;
														end if;
														
														
			when STATE_WRITE_INC			=>		nextState <= STATE_IDLE;
	
															
			when STATE_READ_START	 	=> 	nextState <= STATE_READ_RAM;
			when STATE_READ_RAM	 		=> 	if(CTS = '1') then 
																nextState <= STATE_READ_RAM;
															else 
																nextState <= STATE_READ_INC;
														end if;
			when STATE_READ_INC			=>		nextState <= STATE_IDLE;
			
			
	  end case;
	
	
	end process;  
	

END Behaviour;