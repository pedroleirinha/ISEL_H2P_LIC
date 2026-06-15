library ieee;
use ieee.std_logic_1164.all;

ENTITY MUX8_4L1 IS
	PORT(
		A: IN std_logic;
		B: IN std_logic;
		C: IN std_logic;
		D: IN std_logic;
		E: IN std_logic;
		F: IN std_logic;
		G: IN std_logic;
		H: IN std_logic;
		S: IN std_logic_vector(3 downto 0);
		Y: OUT std_logic
	);
END MUX8_4L1;

ARCHITECTURE Behaviour OF MUX8_4L1 IS
BEGIN
	
	Y <=  (A AND NOT S(3) AND NOT S(2) AND NOT S(1) AND NOT S(0)) OR
			(B AND NOT S(3) AND NOT S(2) AND NOT S(1) AND S(0)) OR
			(C AND NOT S(3) AND NOT S(2) AND S(1) AND NOT S(0)) OR
			(D AND NOT S(3) AND NOT S(2) AND S(1) AND S(0)) OR
			(E AND NOT S(3) AND S(2) AND NOT S(1) AND NOT S(0)) OR
			(F AND NOT S(3) AND S(2) AND NOT S(1) AND S(0)) OR
			(G AND NOT S(3) AND S(2) AND S(1) AND NOT S(0)) OR
			(H AND NOT S(3) AND S(2) AND S(1) AND S(0));
			
			
	
	
END Behaviour;