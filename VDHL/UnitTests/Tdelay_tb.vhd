library IEEE;
use IEEE.std_logic_1164.all;

entity Tdelay_tb is
end entity;

architecture Tdelay_tb_arch of Tdelay_tb is

    
    component Tdelay PORT(
        clk_in : IN  std_logic;
        ce     : IN  std_logic;
        reset  : IN  std_logic;
        delay  : IN  std_logic_vector(1 downto 0);
        S      : OUT std_logic
    );
    end component;

   
    signal clk_in_TB : std_logic := '0';
    signal ce_TB     : std_logic := '0';
    signal reset_TB  : std_logic := '0';
    signal delay_TB  : std_logic_vector(1 downto 0) := "00";
    signal S_TB      : std_logic;

    constant clk_period : time := 20 ns;

begin

    
    UUT: Tdelay port map(    
        clk_in => clk_in_TB,
        ce     => ce_TB,
        reset  => reset_TB,
        delay  => delay_TB,
        S      => S_TB
    );
    
  
    clk_gen: process
    begin
        clk_in_TB <= '0';
        wait for clk_period/2;
        clk_in_TB <= '1';
        wait for clk_period/2;
    end process;
    
   
    stimulus: process 
    begin
        
        reset_TB <= '1';
        ce_TB    <= '0';
        delay_TB <= "00"; 
        wait for 40 ns;
        
        
        reset_TB <= '0'; 
        wait for 20 ns;

        -- CENÁRIO 1: Tecla premida o tempo todo (0.5s)
        ce_TB <= '1'; 
        wait until S_TB = '1' for 600 ns; 
        
        wait for 100 ns; 
        
        -- Larga a tecla
        ce_TB    <= '0';
        reset_TB <= '1';
        wait for 60 ns;
        
        -- CENÁRIO 2: Tecla premida para 1.0s, mas desiste a meio
        reset_TB <= '0';  
        delay_TB <= "01"; 
        wait for 20 ns;
        
        ce_TB <= '1';     
        wait for 120 ns; 
        
        ce_TB    <= '0';  
        reset_TB <= '1';  
        wait for 200 ns;

      
        wait;
    end process;

end architecture;
