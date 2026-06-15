library ieee;
use ieee.std_logic_1164.all;

ENTITY KeyTransmitter IS
	PORT(
		CLK ,TxClk, Load, CLEAR:IN std_logic;
		D:		 						IN std_logic_vector(3 downto 0);
		TxD, KbFree:				OUT std_logic
	);
END KeyTransmitter;

ARCHITECTURE Behaviour OF KeyTransmitter IS
		
	component KeyTransmitterControl
		PORT(
			clk_in, Load, CLEAR, CE, zeros: IN std_logic;
			kbFree, counterEnable, PL, muxBit, counterReset: OUT std_logic
		);
	end component;
	
	component MUX2_1L7
		PORT(
			A,B: IN std_logic_vector (6 downto 0);
			S: IN std_logic;
			Y: OUT std_logic_vector (6 downto 0)
		);
	end component;
	
	COMPONENT Counter IS
        PORT (
			clk_in, CE, CLEAR, PL: IN std_logic;
			initial, step, TcValue: IN std_logic_vector (3 downto 0);
			Q: OUT std_logic_vector (3 downto 0);
			Z, TC: OUT std_logic
        );
	END COMPONENT;
	
	COMPONENT RegistryL4 IS
		PORT(
			D: 							IN std_logic_vector (3 downto 0);
			clk_in, CE, CLEAR, SET: IN std_logic;
			Q: 							OUT std_logic_vector (3 downto 0)
		);
	END COMPONENT;
	
	component MUX8_4L1
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
	end component;
	
	signal PL, counterFinished, counterEnable, TxdBit, TxDFinal, muxBit, counterReset, Reset: std_logic;
	signal muxOut, muxA, muxB: std_logic_vector(6 downto 0);
	signal counter_steps, key: std_logic_vector(3 downto 0);
	
BEGIN

	registry: RegistryL4 port map(
		clk_in => CLK,
		CLEAR  => CLEAR,
		SET	=> '0',
		D 		=> D,
		CE 	=> PL,
		Q 		=> key
	);

	mux: MUX8_4L1 port map(
		A => '0',
		B => '1',
		C => key(3),
		D => key(2),
		E => key(1),
		F => key(0),
		G => '0',
		H => '1',
		S => counter_steps,
		Y => TxdBit
	);
	
	muxA <= "000000" & '1';
	muxB <= "000000" & TxdBit;
	
	
	holdregister: MUX2_1L7 port map(
		A 		=> muxA,
		B		=> muxB,
		S		=> muxBit,
		Y		=> muxOut
	);
	
	TxDFinal <= muxOut(0);
		
	Reset <= counterReset OR CLEAR;
	
	contador_Delay: Counter port map(
		clk_in  => TxCLK,
		CE      => counterEnable,        
		TcValue => "0111",
		CLEAR   => Reset,     
		PL      => '0',           
		initial => "0000",
		step    => "0001",
		Q       => counter_steps,
		TC		  => counterFinished
	);  

	control: KeyTransmitterControl port map(
		clk_in 			=> CLK,
		CE 				=> '1',
		Load 				=> Load,
		zeros				=> counterFinished,
		CLEAR				=> CLEAR,
		kbFree 			=> KbFree,
		counterEnable	=> counterEnable,
		PL					=> PL,
		muxBit			=> muxBit,
		counterReset	=> counterReset
	);
		
	
	TxD <= TxDFinal;
	
	

END Behaviour;