# STM32-Bootloader
 STM32 bootloader example that can jump to 2 apps.
  
## Hardware
![alt text](https://github.com/viktorvano/STM32-Bootloader/blob/master/images/STM32F103C8T6_Top.jpg?raw=true)  
  
![alt text](https://github.com/viktorvano/STM32-Bootloader/blob/master/images/STM32F103C8T6_Bottom.jpg?raw=true)  
  
![alt text](https://github.com/viktorvano/STM32-Bootloader/blob/master/images/STM32F103C8T6_Switch_Macro_Angle.jpg?raw=true)  
  
![alt text](https://github.com/viktorvano/STM32-Bootloader/blob/master/images/STM32F103C8T6_Switch_Macro_Side.jpg?raw=true)  
  
  
## Software  
  
This bootloader example can jump to 2 different applications.  
  
The FLASH memory (64KB) is splitted to multiple partitions.  
The first partition (20KB) is for the bootloader.  
The second partition is for the Application1 (22KB).
The third partion is for the Application2 (22KB).  
  
## Bootloader
  
A bootloader is just an app that can jump to another application, erase the flash, or write a new data to the FLASH memory.  
Applications are located in different sections of the FLASH memory, after the last bootloader sector ends.  
Therefore applications need to have shifted the FLASH memory origin and offset in the vector table.  
  
###### Linker - FLASH.ld
Keep the flash origin, but change the size of the flash memory according to the bootloader size.  
This bootloader has size under 20KB. Applocations can start right from the next sector.  
```C
/* Memories definition */
MEMORY
{
  RAM    (xrw)    : ORIGIN = 0x20000000,   LENGTH = 20K
  FLASH    (rx)    : ORIGIN = 0x8000000,   LENGTH = 20K /*64K*/
}
```
  
###### C Code - Bootloader
  
```C
#define APP1_START (0x08005000)			//Origin + Bootloader size (20kB)
#define APP2_START (0x0800A800)			//Origin + Bootloader size (20kB) + App1 Bank (22kB)
#define FLASH_BANK_SIZE (0X5800)		//22kB
#define FLASH_PAGE_SIZE_USER (0x400)	//1kB

typedef struct
{
    uint32_t		stack_addr;     // Stack Pointer
    application_t*	func_p;        // Program Counter
} JumpStruct;

void jumpToApp(const uint32_t address)
{
	const JumpStruct* vector_p = (JumpStruct*)address;

	deinitEverything();

	/* let's do The Jump! */
    /* Jump, used asm to avoid stack optimization */
    asm("msr msp, %0; bx %1;" : : "r"(vector_p->stack_addr), "r"(vector_p->func_p));
}

void deinitEverything()
{
	//-- reset peripherals to guarantee flawless start of user application
	HAL_GPIO_DeInit(LED_GPIO_Port, LED_Pin);
	HAL_GPIO_DeInit(USB_ENABLE_GPIO_Port, USB_ENABLE_Pin);
	USBD_DeInit(&hUsbDeviceFS);
	  __HAL_RCC_GPIOC_CLK_DISABLE();
	  __HAL_RCC_GPIOD_CLK_DISABLE();
	  __HAL_RCC_GPIOB_CLK_DISABLE();
	  __HAL_RCC_GPIOA_CLK_DISABLE();
	HAL_RCC_DeInit();
	HAL_DeInit();
	SysTick->CTRL = 0;
	SysTick->LOAD = 0;
	SysTick->VAL = 0;
}
```
  
## Applications
###### Linker - FLASH.ld
###### C Code - Applications
  
