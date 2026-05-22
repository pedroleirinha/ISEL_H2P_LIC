library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

ENTITY Time_Delay IS
    PORT (
        clk      : IN  std_logic;                    
        KeyPress : IN  std_logic;                    
        Mux_select   : IN  std_logic_vector(1 downto 0); 
        Pulse : OUT std_logic
    );
END Time_Delay;


ARCHITECTURE Structural OF Time_Delay IS



	COMPONENT CLKDIV IS
        GENERIC ( div: natural := 50000 );
        PORT ( 
            clk_in  : IN  std_logic;
            clk_out : OUT std_logic
        );
	END COMPONENT;
	
	
	COMPONENT FFD IS
        PORT (	
            CLK   : IN  std_logic;
            RESET : IN  std_logic;
            SET   : IN  std_logic;
            D     : IN  std_logic;
            EN    : IN  std_logic;
            Q     : OUT std_logic
        );
    END COMPONENT;
	 
	 
	COMPONENT Counter IS
        PORT (
            clk_in, CE, CLEAR, PL: IN std_logic;
            initial, step: IN std_logic_vector (3 downto 0);
            Q: OUT std_logic_vector (3 downto 0);
            Z: OUT std_logic
        );
	END COMPONENT;
	 
	 
	COMPONENT MUX4_2L1 IS
        PORT (
            A: IN std_logic_vector(3 downto 0);
            S: IN std_logic_vector(1 downto 0);
            Y: OUT std_logic
        );
	END COMPONENT;

	 
	 
	signal clkdiv_wave     : std_logic;
	signal clkdiv_old_wave : std_logic;
	signal clk_pulse       : std_logic;
	
	signal counter_steps  : std_logic_vector(3 downto 0); 
   signal mux_inputs     : std_logic_vector(3 downto 0); 
   signal Tdelay_reached : std_logic;
	
	signal not_KeyPress   : std_logic;
   signal mux_pl         : std_logic;
	
	
BEGIN


	clock_Delay: CLKDIV 
        generic map ( div => 12500000 )
        port map (
            clk_in  => clk,
            clk_out => clkdiv_wave
        );

		  
ffd_Delay: FFD  port map (
            CLK   => clk,
            RESET => '0',          
            SET   => '0',          
            EN    => '1',          
            D     => clkdiv_wave,  
            Q     => clkdiv_old_wave 
        );


		contador_Delay: Counter port map(
        clk_in  => clk,
        CE      => clk_pulse,        
        CLEAR   => not_KeyPress,     
        PL      => mux_pl,           
        initial => "0000",
        step    => "0001",
        Q       => counter_steps                
    );  

	 
	 	mux_Delay: MUX4_2L1 port map(
        A => mux_inputs,
        S => Mux_select,
        Y => Tdelay_reached  
    );
	 
	 
	 clk_pulse <= clkdiv_wave AND (NOT clkdiv_old_wave);
	 
	 not_KeyPress <= NOT KeyPress;
	 
	 mux_pl       <= Tdelay_reached;
	 
	 
	 
	 mux_inputs(0) <= counter_steps(0);                  
	 mux_inputs(1) <= counter_steps(1);                  
 	 mux_inputs(2) <= counter_steps(0) AND counter_steps(1); 
  	 mux_inputs(3) <= counter_steps(2);                  
	 
	 Pulse <= Tdelay_reached AND clk_pulse;
	 
END Structural;
