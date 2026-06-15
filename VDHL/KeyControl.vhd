library ieee;
use ieee.std_logic_1164.all;

ENTITY KeyControl IS
    PORT(
        clk_in, Kpress, Kack, CLEAR, CE: IN std_logic;
        time_up:                         IN std_logic;  
        Kval, Kscan:                     OUT std_logic;
        ceTimer, resetTimer:           OUT std_logic  
    );
END KeyControl;

ARCHITECTURE Behaviour OF KeyControl IS

    type STATE_TYPE is (STATE_SCANNING_00, STATE_READING_01, STATE_ACKNOWLEDGE_10);
    signal currState, nextState: STATE_TYPE;

BEGIN

   
    currState <= STATE_SCANNING_00 when CLEAR = '1' else nextState when rising_edge(clk_in);

	 
    generateNextState: process(Kpress, Kack, time_up, CE, currState)
    begin
        nextState <= currState; 
        
        case currState is
			
            when STATE_SCANNING_00		=>		if (Kpress = '1' AND CE = '1') then
																nextState <= STATE_READING_01;
															else
																nextState <= STATE_SCANNING_00;
															end if;

														
            when STATE_READING_01		=>    if (Kack = '1') then
																nextState <= STATE_ACKNOWLEDGE_10;
															else
																nextState <= STATE_READING_01;
															end if;

														
--            when STATE_ACKNOWLEDGE_10	=>		if (Kack = '0') then 
--																nextState <= STATE_SCANNING_00;
--																
--															if (Kpress = '0') then
--																	nextState <= STATE_ACKNOWLEDGE_10;
																
--																elsif (time_up = '0') then
--																		nextState <= STATE_ACKNOWLEDGE_10;
--																	else
--																		nextState <= STATE_READING_01;

															--		end if;
--															--	end if;
--															end if;

            when STATE_ACKNOWLEDGE_10	=>		if (Kack = '0') then
																if (Kpress = '0') then
																	nextState <= STATE_SCANNING_00;
																elsif (time_up = '0') then
																		nextState <= STATE_ACKNOWLEDGE_10;
																	else
																		nextState <= STATE_READING_01;
 
															--		end if;
															--	end if;
																end if;
															elsif (Kack = '1') then
																	nextState <= STATE_ACKNOWLEDGE_10;
															end if;

				--when STATE_CLEAN_TIMER	=>		nextState <= STATE_READING_01; 


        end case;
    end process; 



    --ceTimer <= '1' when (ACKNOWLEDGE_10 AND Kpress = '1') else '0';
    --resetTimer <= '1' when (currState = STATE_CLEAN_TIMER OR CLEAR = '1' or Kpress = '0' ) else '0';


    Kscan <= '1' when (currState = STATE_SCANNING_00 AND Kpress = '0') else '0';	 
    Kval  <= '1' when (currState = STATE_READING_01) else '0';
	 --resetTimer <= '1' when (currState = STATE_READING_01 or (currState = STATE_ACKNOWLEDGE_10 and Kpress = '0') ) else '0';
	 resetTimer <= '1' when (currState = STATE_READING_01 ) else '0';
	 ceTimer <= '1' when (currState = STATE_ACKNOWLEDGE_10 and Kpress = '1' and Kack = '0') else '0';
	 
	 
END Behaviour;
