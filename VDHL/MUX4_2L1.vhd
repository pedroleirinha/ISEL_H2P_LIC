library ieee;
use ieee.std_logic_1164.all;

ENTITY MUX4_2L1 IS
	PORT(
		A: IN std_logic_vector(3 downto 0);
		S: IN std_logic_vector(1 downto 0);
		Y: OUT std_logic
	);
END MUX4_2L1;

ARCHITECTURE Behaviour OF MUX4_2L1 IS
BEGIN
	
	Y <= (A(0) AND NOT S(0) AND NOT S(1)) OR
			(A(1) AND S(0) AND NOT S(1)) OR
			(A(2) AND NOT S(0) AND S(1)) OR
			(A(3) AND S(0) AND S(1));
	
	
END Behaviour;