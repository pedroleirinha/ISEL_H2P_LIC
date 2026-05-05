library ieee;
use ieee.std_logic_1164.all;

ENTITY MUX2_1L1 IS
	PORT(
		A,B: IN std_logic;
		S: IN std_logic;
		Y: OUT std_logic
	);
END MUX2_1L1;

ARCHITECTURE Behaviour OF MUX2_1L1 IS
BEGIN
	Y <= (A AND NOT S) OR (B AND S);
	
END Behaviour;