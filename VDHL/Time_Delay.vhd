library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

ENTITY Time_Delay IS
    PORT (
        clk, ceTimer, resetTimer: 	IN  std_logic;                                 
        delays: 							IN  std_logic_vector(1 downto 0); 
        timeUp: 							OUT std_logic
    );
END Time_Delay;


ARCHITECTURE Structural OF Time_Delay IS

	COMPONENT CLKDIV IS
        GENERIC ( div: natural := 50 );
        PORT ( 
            clk_in  : IN  std_logic;
            clk_out : OUT std_logic
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
	 
	COMPONENT MUX4_2L4 IS
        PORT (
            A, B, C, D: IN std_logic_vector(3 downto 0);
            S: IN std_logic_vector(1 downto 0);
            Y: OUT std_logic_vector(3 downto 0)
        );
	END COMPONENT;

	 
	 
	signal clkdiv_wave     : std_logic;
	
	signal counter_steps,saida  : std_logic_vector(3 downto 0); 
   signal mux_inputs     : std_logic_vector(3 downto 0); 
   signal Tdelay_reached : std_logic;
	
	
BEGIN


	clock_Delay: CLKDIV 
		port map (
			clk_in  => clk,
			clk_out => clkdiv_wave
	);

	contador_Delay: Counter port map(
		clk_in  => clkdiv_wave,
		CE      => ceTimer,        
		CLEAR   => resetTimer,     
		PL      => '0',           
		initial => "0000",
		step    => "0001",
		Q       => counter_steps                
	);  

	
	mux_Delay: MUX4_2L4 port map(
		A => "0001",
		B => "0010",
		C => "0011",
		D => "0100",
		S => delays,
		Y => saida
	);
	
	Tdelay_reached <= (saida(0) XNOR counter_steps(0)) AND 
							(saida(1) XNOR counter_steps(1)) AND 
							(saida(2) XNOR counter_steps(2)) AND 
							(saida(3) XNOR counter_steps(3));               

	timeUp <= Tdelay_reached;

END Structural;
