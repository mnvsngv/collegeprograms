# System Programming and Compiler Construction
To run the samples, execute the appropriate command from the root directory.

## Code Generation
`gradlew --quiet SPCC:executeCodeGeneration`

Sample Input:
`d=(a-b)+(a-c)+(a-c)`

## Lexical Analyzer
`gradlew --quiet SPCC:executeLexicalAnalyzer`

## Macro Processor
`gradlew --quiet SPCC:executeMacroProcessor`

## Recursive Descent Parser
`gradlew --quiet SPCC:executeRecursiveDescentParser`

## Loop Optimization
`gradlew --quiet SPCC:executeLoopOptimization`

### Input
Input is taken from [input.txt](LoopOptimization/input.txt).

### Samples
For more input samples, check out the [samples](LoopOptimization/Sample Inputs).

## Toy Compiler
`gradlew --quiet SPCC:executeToyCompiler`

### Input
Input is taken from [input.txt](ToyCompiler/input.txt).

### Samples
For more input samples, check out the [samples](ToyCompiler/Sample inputs.txt).

## Two Pass Assembler
`gradlew --quiet SPCC:executeTwoPassAssembler`