library ieee;
use ieee.std_logic_1164.all;

ENTITY KeyDecode IS
	PORT(
		clk_in, Kack, CLEAR: 	IN std_logic;
		delay:               	IN std_logic_vector(1 downto 0); 
		rows: 						IN std_logic_vector(3 downto 0);
		cols: 						OUT std_logic_vector(3 downto 0);
		K: 							OUT std_logic_vector (3 downto 0);
		Kval:							OUT std_logic
	);
END KeyDecode;

ARCHITECTURE Behaviour OF KeyDecode IS
	component KeyScan
		PORT(
			clk_in, Kscan, CLEAR:IN std_logic;
			rows: 					IN std_logic_vector(3 downto 0);
			cols: 					OUT std_logic_vector(3 downto 0);
			K: 						OUT std_logic_vector (3 downto 0);
			Kpress:					OUT std_logic
		);
	end component;
	
	component KeyControl 
	  PORT(
			clk_in, Kpress, Kack, CLEAR, CE: IN std_logic;
			time_up:                         IN std_logic;  
			Kval, Kscan:                     OUT std_logic;
			ce_timer, reset_timer:           OUT std_logic  
	  );
	end component;


	component Time_Delay
	  PORT(
			clk      : IN  std_logic;                    
            KeyPress : IN  std_logic;                    
            Mux_select   : IN  std_logic_vector(1 downto 0); 
            Pulse : OUT std_logic		 
	  );
	end component;

	signal controlKpress, controlKscan : std_logic;
	signal s_time_up    : std_logic;
    signal s_ce_timer   : std_logic;
    signal s_reset_timer: std_logic;

BEGIN
	
	scan: KeyScan port map(
		clk_in 	=> clk_in,
		Kscan 	=> controlKscan,
		rows 		=> rows,		
		cols 		=> cols,		
		CLEAR		=> CLEAR,
		K 			=> K,		
		Kpress 	=>	controlKpress
	);
	
	control: KeyControl port map(
	  clk_in      => clk_in,
	  Kpress      => controlKpress,
	  Kack        => Kack,
	  CLEAR       => CLEAR,
	  CE          => '1',
	  time_up     => s_time_up,     
	  Kval        => Kval,
	  Kscan       => controlKscan,
	  ce_timer    => s_ce_timer,    
	  reset_timer => s_reset_timer  
	);


	time_D: Time_Delay port map(
	  clk        => clk_in,
	  KeyPress   => controlKpress,  
	  Mux_select => delay,          
	  Pulse      => s_time_up           
	);

END Behaviour;
