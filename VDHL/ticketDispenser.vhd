LIBRARY IEEE;
use IEEE.STD_LOGIC_1164.all;

entity TICKET_DISPENSER is

	port ( Prt, CollectTicket: in STD_LOGIC;
			 Dout: in STD_LOGIC_VECTOR(8 downto 0);
			 Fn: out STD_LOGIC;
			 HEX0, HEX1, HEX2, HEX3, HEX4, HEX5: out STD_LOGIC_VECTOR(7 downto 0) );
			
end TICKET_DISPENSER;

architecture arq_TICKET_DISPENSER of TICKET_DISPENSER is

component DECODERHEX 

	port ( A: in STD_LOGIC_VECTOR(3 downto 0);
			 ewr: in STD_LOGIC_VECTOR(7 downto 0);
			 clear: in STD_LOGIC;
			 HEX0: out STD_LOGIC_VECTOR(7 downto 0) );		
			
end component;

signal RT, decodeClear: STD_LOGIC;
signal RTtoBDC, O, D: STD_LOGIC_VECTOR(3 downto 0);

begin

	-- RT bit indicates either is a one-way ticket (0) or a two-way ticket (1)
	RT <= Dout(0);
	-- Destination Station Code
	D <= Dout(4) & Dout(3) & Dout(2) & Dout(1);
	-- Origin Station Code
	O <= Dout(8) & Dout(7) & Dout(6) & Dout(5);
	
	-- Activate Fn when client retrieves the printed ticket
	Fn <= CollectTicket;
	
	-- Convert RT bit to BDC
	RTtoBDC <= "0000" when RT = '1' else "0001";
	
	
	decodeClear <= not Prt;
	
	-- Using the six 7 segment displays available on the MAX10 Lite Board
	-- produce a valid array of numbers and letters which the client can understand
	-- Valid format example: [b0o1d4]
	-- Means: "bilhete de ida e volta, da estação de origem com o código (0001) para a estação de destino com o código (0100)
	-- Implementation on the six 7 displays segment, facing the board, starting from the right to the left:	
	U0: DECODERHEX port map (
		A => D,
		ewr => "11111111",
		clear => decodeClear,
		HEX0 => HEX4 );
		
	U1: DECODERHEX port map (
		-- Always d in BDC
		A => "1101",
		ewr => "11111111",
		clear => '0',
		HEX0 => HEX5 );

	U2: DECODERHEX port map (
		A => O,
		ewr => "11111111",
		clear => decodeClear,
		HEX0 => HEX2 );
	
	U3: DECODERHEX port map (
		A => "0000",
		-- A value assignment is redundant here, since the value in ewr will overwrite it, because its different from "11111111"
		-- Always o, which has no representation in BDC 
		ewr => "10100011",
		clear => '0',
		HEX0 => HEX3 );
		
	U4: DECODERHEX port map (
		-- RT bit in BDC
		A => RTtoBDC,
		ewr => "11111111",
		clear => decodeClear,
		HEX0 => HEX0 );
	
	U5: DECODERHEX port map (
		-- Always b in BDC
		A => "1011",
		ewr => "10110110",
		clear => '0',
		HEX0 => HEX1 );
							
end arq_TICKET_DISPENSER;