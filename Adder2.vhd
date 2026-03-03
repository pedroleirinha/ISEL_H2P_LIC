library ieee;
use ieee.std_logic_1164.all;

ENTITY Adder2 IS
	PORT(
		A,B: IN std_logic_vector (1 downto 0);
		C0: IN std_logic;
		S: OUT std_logic_vector (1 downto 0);
		C4: OUT std_logic
	);
END Adder2;

ARCHITECTURE Structural OF Adder2 IS
	component FullAdder 
		PORT(
			A,B,CBi: IN std_logic;
			S, CO: OUT std_logic
		);
	end component;
	
	signal carries: std_logic_vector(1 downto 0);
	
BEGIN

	adder1: FullAdder port map(
		A => A(0),
		B => B(0),
		CBi => C0,
		S => S(0),
		CO => carries(0)
	);
	
	adder2: FullAdder port map(
		A => A(1),
		B => B(1),
		CBi => carries(0),
		S => S(1),
		CO => carries(1)
	);
	
	C4 <= carries(1);

	
END Structural;