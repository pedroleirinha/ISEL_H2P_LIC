transcript on
if {[file exists rtl_work]} {
	vdel -lib rtl_work -all
}
vlib rtl_work
vmap work rtl_work

vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/UsbPort.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/KeyScan.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/MUX2_1L4.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/RegistryL2.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/Counter.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/Adder.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/RegistryL4.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/FullAdder.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/FFD.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/decoder.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/MUX4_2L1.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/KeyControl.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/KeyDecode.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/TicketMachine.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/SerialReceiver.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/ShiftRegister.vhd}
vcom -93 -work work {C:/Users/oleir/Desktop/Coding/TicketMachine/VDHL/HoldRegister.vhd}

