'use strict';

import { GraphPanelRendererBase } from './graph-panel-renderer-base.js';
import { GraphPanelStyler } from './graph-panel-styler.js';

class ConnectionHinterService extends GraphPanelRendererBase {
  constructor($rootScope, WorkflowService, OperationsHierarchyService, Operations) {
    super();
    this.$rootScope = $rootScope;
    this.WorkflowService = WorkflowService;
    this.OperationsHierarchyService = OperationsHierarchyService;
    this.Operations = Operations;
  }

  /*
   * Highlights such ports that match to the given port
   * and colours ports that don't match.
   */
  showHints(sourceEndpoint, renderMode) {
    const workflow = this.WorkflowService.getWorkflow();
    const nodes = workflow.getNodes();

    const sourceNodeId = sourceEndpoint.getParameter('nodeId');
    const sourceNode = workflow.getNodeById(sourceNodeId);
    const sourcePortIndex = sourceEndpoint.getParameter('portIndex');
    const sourcePort = sourceNode.output[sourcePortIndex];

    const getInputPorts = (endpoints) => _.filter(endpoints, (endpoint) => endpoint.isTarget);
    const highlightInputPorts = (endpoint, node) => {
      const portIndex = endpoint.getParameter('portIndex');
      const port = node.input[portIndex];
      const typesMatch = this.OperationsHierarchyService.IsDescendantOf(sourcePort.typeQualifier, port.typeQualifier);

      if (typesMatch && // types match
        endpoint.connections.length === 0 && // there cannot be any edge attached
        port.nodeId !== sourceNodeId) // attaching an edge to the same node is forbidden
      {
        GraphPanelStyler.styleInputEndpointTypeMatch(endpoint, renderMode);
      } else {
        GraphPanelStyler.styleInputEndpointTypeDismatch(endpoint, renderMode);
      }
    };

    _.forEach(nodes, (node) => {
      const nodeEl = this.getNodeById(node.id);
      const endpoints = jsPlumb.getEndpoints(nodeEl);
      _.forEach(getInputPorts(endpoints), (endpoint) => {
        highlightInputPorts(endpoint, node);
      });
    });

    GraphPanelStyler.styleSelectedOutputEndpoint(sourceEndpoint);
  }

  /*
   * Remove any port highlighting.
   */
  setDefaultPortColors(renderMode) {
    const nodes = this.WorkflowService.getWorkflow().getNodes();
    _.forEach(nodes, (node) => {
      const nodeEl = this.getNodeById(node.id);
      const endpoints = jsPlumb.getEndpoints(nodeEl);
      _.forEach(endpoints, (endpoint) => {
        if (endpoint.isSource) {
          GraphPanelStyler.styleInputEndpointDefault(endpoint, renderMode);
        }
        if (endpoint.isTarget) {
          GraphPanelStyler.styleOutputEndpointDefault(endpoint, renderMode);
        }
      });
    });
  }

  /*
   * Highlight the operation on the left side panel.
   */
  highlightOperations(sourceEndpoint) {
    const workflow = this.WorkflowService.getWorkflow();
    const sourceNodeId = sourceEndpoint.getParameter('nodeId');
    const sourceNode = workflow.getNodeById(sourceNodeId);
    const sourcePortIndex = sourceEndpoint.getParameter('portIndex');
    const sourcePort = sourceNode.output[sourcePortIndex];

    const operations = this.Operations.getData();
    let operationsMatch = {};
    _.forEach(operations, (operation) => {
      const inputMatches = _.reduce(operation.ports.input, (acc, input) => {
        acc.push(this.OperationsHierarchyService.IsDescendantOf(sourcePort.typeQualifier, input.typeQualifier));
        return acc;
      }, []);

      operationsMatch[operation.id] = _.any(inputMatches);
    });

    this.$rootScope.$broadcast('ConnectionHinter.HIGHLIGHT_OPERATIONS', operationsMatch);
  }

  disableHighlightingOoperations() {
    this.$rootScope.$broadcast('ConnectionHinter.DISABLE_HIGHLIGHTINGS');
  }
}

exports.inject = function (module) {
  module.factory('ConnectionHinterService', /* @ngInject */($rootScope, WorkflowService, OperationsHierarchyService, Operations) => {
    return new ConnectionHinterService($rootScope, WorkflowService, OperationsHierarchyService, Operations);
  });
};
