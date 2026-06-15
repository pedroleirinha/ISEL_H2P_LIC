library IEEE;
use IEEE.std_logic_1164.all;

entity Tdelay_tb is
end entity;

architecture Tdelay_tb_arch of Tdelay_tb is

    
    component Time_Delay PORT (
        clk, ceTimer, resetTimer: 	IN  std_logic;                                 
        delays: 							IN  std_logic_vector(1 downto 0); 
        timeUp: 							OUT std_logic
    );
    end component;

   
	-- UUT signals
	signal CLK_TB : std_logic := '0';
	signal CLEAR_TB, CE_TIMER_TB, RESET_TIMER_TB, TIME_UP_TB: std_logic;
	signal Q_TB: std_logic_vector(9 downto 0);
	signal DELAY_TB: std_logic_vector(1 downto 0);

	constant MCLK_PERIOD : time := 20 ns;
	constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
	constant CLK_PERIOD : time := 20 ns;


begin
	 CLK_TB <= not CLK_TB after MCLK_HALF_PERIOD;		
    
    UUT: Time_Delay PORT map (
        clk => CLK_TB,
		  ceTimer => CE_TIMER_TB,
		  resetTimer => RESET_TIMER_TB,
        delays		=> DELAY_TB,
        timeUp		=> TIME_UP_TB
    );  
   
    stimulus: process 
    begin
        
      RESET_TIMER_TB <= '1';
      CE_TIMER_TB    <= '0';
      DELAY_TB <= "00"; 
		wait for CLK_PERIOD;
		RESET_TIMER_TB <= '0';
      CE_TIMER_TB    <= '1';
		wait for 500000 ps;
		
      RESET_TIMER_TB <= '1';
      DELAY_TB <= "01"; 
		wait for CLK_PERIOD;
		RESET_TIMER_TB <= '0';
		CE_TIMER_TB    <= '1';
		wait for 2000000 ps;
		
		RESET_TIMER_TB <= '1';
      DELAY_TB <= "10"; 
		wait for CLK_PERIOD;
		RESET_TIMER_TB <= '0';
		CE_TIMER_TB    <= '1';
		wait for 5000000 ps;
		
		RESET_TIMER_TB <= '1';
      DELAY_TB <= "11"; 
		wait for CLK_PERIOD;
		RESET_TIMER_TB <= '0';
		CE_TIMER_TB    <= '1';
		wait for 8000000 ps;
		

      
        wait;
    end process;

end architecture;
