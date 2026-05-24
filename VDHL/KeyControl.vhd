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

    type STATE_TYPE is (STATE_SCANNING, STATE_READING, STATE_ACKNOWLEDGE, STATE_CLEAN_TIMER);
    signal currState, nextState: STATE_TYPE;

BEGIN

   
    currState <= STATE_SCANNING when CLEAR = '1' else nextState when rising_edge(clk_in);

    generateNextState: process(Kpress, Kack, time_up, CE, currState)
    begin
        nextState <= currState; 
        
        case currState is
            when STATE_SCANNING => 
                if (Kpress = '1' AND CE = '1') then 
                    nextState <= STATE_READING;  
                end if;

            when STATE_READING => 
                if (Kack = '1') then 
                    nextState <= STATE_ACKNOWLEDGE; 
                end if;

            when STATE_ACKNOWLEDGE => 
                if (Kpress = '0') then
                    nextState <= STATE_SCANNING;     
                elsif (Kpress = '1') then
                     if (time_up = '1') then
								nextState <= STATE_CLEAN_TIMER;      
							elsif (time_up = '0') then
								nextState <= STATE_ACKNOWLEDGE;      
							end if;
                end if;
				when STATE_CLEAN_TIMER => nextState <= STATE_READING; 
                
        end case;
    end process;  
      
    Kscan <= '1' when (currState = STATE_SCANNING AND Kpress = '0') else '0';
    Kval  <= '1' when (currState = STATE_READING) else '0';
      
    
    ceTimer <= '1' when (currState = STATE_ACKNOWLEDGE AND Kpress = '1') else '0';
    resetTimer <= '1' when (currState = STATE_CLEAN_TIMER OR CLEAR = '1' or Kpress = '0') else '0';
    
END Behaviour;
