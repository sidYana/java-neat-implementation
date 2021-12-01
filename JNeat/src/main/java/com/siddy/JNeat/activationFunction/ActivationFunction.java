package com.siddy.JNeat.activationFunction;

public enum ActivationFunction implements ActivationFunctionIF {

	SIGMOID {
		/*
		 * sigmoid(x) = 1/(1+e^-x)
		 * 
		 * d sigmoid(x)/dx = sigmoid(x)(1-sigmoid(x))
		 */
		@Override
		public double applyActivation(double input) {
			return (1.0 / (1.0 + Math.exp(-input)));
		}
		
		@Override
		public String getName() {
			return name();
		}

		@Override
		public double applyActivationDerivative(double input) {
			//if input already sigmoided
			return input*(1.0-input);
		}
	},
	TANH {
		
		/*
		 * tanh(x)
		 * 
		 * d tanh(x)/dx = 1-(tanh(x))^2
		 * 
		 */
		
		@Override
		public double applyActivation(double input) {
			return 2.0 * SIGMOID.applyActivation(2.0 * input) - 1.0;
		}
		
		@Override
		public String getName() {
			return name();
		}

		@Override
		public double applyActivationDerivative(double input) {
			//assuming activation was already applied
			return (1.0-Math.pow(input, 2));
		}
	},
	ReLU {
		
		/*
		 * ReLU = z if z > 0 else 0
		 * 
		 * d ReLU/dx = 1 if z>0 else 0
		 */
		
		@Override
		public double applyActivation(double input) {
			return Math.max(0, input);
		}
		
		@Override
		public String getName() {
			return name();
		}

		@Override
		public double applyActivationDerivative(double input) {
			//assuming activation was already applied
			return (input > 0)?1:0;
		}
	},
	LEAKY_ReLU {
		
		/*
		 * Leaky_ReLU = z if z > 0 else az
		 * 
		 * d Leaky_ReLU/dx = 1 if z>0 else a
		 */
		
		private float constant = 0.1f;
		
		@Override
		public double applyActivation(double input) {
			return (input < 0) ? (constant * input) : (input);
		}
		
		@Override
		public String getName() {
			return name();
		}

		@Override
		public double applyActivationDerivative(double input) {
			return (input > 0)?1:constant;
		}
	},
	LINEAR {
		/*
		 * y=mx
		 * dy/dx = m
		 * here m=1 
		 */
		@Override
		public double applyActivation(double input) {
			return input;
		}
		
		@Override
		public String getName() {
			return name();
		}

		@Override
		public double applyActivationDerivative(double input) {
			return 1;
		}
	},
	BINARY_STEP {
		@Override
		public double applyActivation(double input) {
			return (input < 0) ? 0 : 1;
		}
		
		@Override
		public String getName() {
			return name();
		}

		@Override
		public double applyActivationDerivative(double input) {
			return 0;
		}
	},
	ELU {
		private float constant = 0.1f;
		@Override
		public double applyActivation(double input) {
			return (input < 0) ? (constant *(Math.exp(input) - 1.0)) : (input);
		}
		
		@Override
		public String getName() {
			return name();
		}

		@Override
		public double applyActivationDerivative(double input) {
			return (input > 0) ? 1.0 : constant*Math.exp(input);
		}
	},
	SINE {

		@Override
		public double applyActivation(double input) {
			return Math.sin(input);
		}

		@Override
		public double applyActivationDerivative(double input) {
			return Math.cos(input);
		}

		@Override
		public String getName() {
			return name();
		}
		
	},
	COS {

		@Override
		public double applyActivation(double input) {
			return Math.cos(input);
		}

		@Override
		public double applyActivationDerivative(double input) {
			return -Math.sin(input);
		}

		@Override
		public String getName() {
			return name();
		}
		
	},
	SINC {

		@Override
		public double applyActivation(double input) {
			return (input == 0) ? 1 : SINE.applyActivation(input) / input;
		}

		@Override
		public double applyActivationDerivative(double input) {
			return (input == 0) ? 0 : ((COS.applyActivation(input) / input) - (SINE.applyActivation(input)/Math.pow(input, 2)));
		}

		@Override
		public String getName() {
			return name();
		}
		
	},
	GAUSSIAN {

		@Override
		public double applyActivation(double input) {
			return Math.exp(-Math.pow(input, 2));
		}

		@Override
		public double applyActivationDerivative(double input) {
			return -2.0 * input * GAUSSIAN.applyActivation(input);
		}

		@Override
		public String getName() {
			return name();
		}
		
	}
}
