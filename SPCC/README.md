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

Input is taken from [input.txt](SPCC/LoopOptimization/input.txt).
For more input samples, check out the [Loop Optimization input samples](SPCC/LoopOptimization/Sample Inputs).

## Toy Compiler
`gradlew --quiet SPCC:executeToyCompiler`

Input is taken from [input.txt](SPCC/ToyCompiler/input.txt).
For more input samples, check out the [Toy Compiler input samples](SPCC/ToyCompiler/Sample inputs.txt).

## Two Pass Assembler
`gradlew --quiet SPCC:executeTwoPassAssembler`