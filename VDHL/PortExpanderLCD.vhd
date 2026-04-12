library ieee;
use ieee.std_logic_1164.all;

ENTITY PortExpanderLCD IS
	PORT(
		SCLK, SDX, SS, CLEAR: 	IN std_logic;
		Q: 							OUT std_logic_vector(9 downto 0)
	);
END PortExpanderLCD;

ARCHITECTURE Behaviour OF PortExpanderLCD IS
	
	component SerialReceiver                           
		PORT(
			SCLK, SDX, SS, CLEAR: 	IN std_logic;
			Q: 							OUT std_logic_vector(9 downto 0)
		);
	end component;
	
BEGIN

	
	serialReceiver1: SerialReceiver port map(
		SCLK 	=> SCLK,
		CLEAR	=> CLEAR,
		SDX 	=> SDX,
		SS 	=> SS,
		Q 		=>	Q
	);
	
END Behaviour;