import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './quote.reducer';

export const QuoteDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const quoteEntity = useAppSelector(state => state.quote.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="quoteDetailsHeading">
          <Translate contentKey="kalitronFurnitureStudioApp.quote.detail.title">Quote</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{quoteEntity.id}</dd>
          <dt>
            <span id="quoteNumber">
              <Translate contentKey="kalitronFurnitureStudioApp.quote.quoteNumber">Quote Number</Translate>
            </span>
          </dt>
          <dd>{quoteEntity.quoteNumber}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="kalitronFurnitureStudioApp.quote.status">Status</Translate>
            </span>
          </dt>
          <dd>{quoteEntity.status}</dd>
          <dt>
            <span id="subtotalMxn">
              <Translate contentKey="kalitronFurnitureStudioApp.quote.subtotalMxn">Subtotal Mxn</Translate>
            </span>
          </dt>
          <dd>{quoteEntity.subtotalMxn}</dd>
          <dt>
            <span id="taxMxn">
              <Translate contentKey="kalitronFurnitureStudioApp.quote.taxMxn">Tax Mxn</Translate>
            </span>
          </dt>
          <dd>{quoteEntity.taxMxn}</dd>
          <dt>
            <span id="totalMxn">
              <Translate contentKey="kalitronFurnitureStudioApp.quote.totalMxn">Total Mxn</Translate>
            </span>
          </dt>
          <dd>{quoteEntity.totalMxn}</dd>
          <dt>
            <span id="laborMxn">
              <Translate contentKey="kalitronFurnitureStudioApp.quote.laborMxn">Labor Mxn</Translate>
            </span>
          </dt>
          <dd>{quoteEntity.laborMxn}</dd>
          <dt>
            <span id="validUntil">
              <Translate contentKey="kalitronFurnitureStudioApp.quote.validUntil">Valid Until</Translate>
            </span>
          </dt>
          <dd>
            {quoteEntity.validUntil ? <TextFormat value={quoteEntity.validUntil} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="publicToken">
              <Translate contentKey="kalitronFurnitureStudioApp.quote.publicToken">Public Token</Translate>
            </span>
          </dt>
          <dd>{quoteEntity.publicToken}</dd>
          <dt>
            <span id="notes">
              <Translate contentKey="kalitronFurnitureStudioApp.quote.notes">Notes</Translate>
            </span>
          </dt>
          <dd>{quoteEntity.notes}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="kalitronFurnitureStudioApp.quote.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{quoteEntity.createdAt ? <TextFormat value={quoteEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="sentAt">
              <Translate contentKey="kalitronFurnitureStudioApp.quote.sentAt">Sent At</Translate>
            </span>
          </dt>
          <dd>{quoteEntity.sentAt ? <TextFormat value={quoteEntity.sentAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.quote.renderImage">Render Image</Translate>
          </dt>
          <dd>{quoteEntity.renderImage ? quoteEntity.renderImage.fileName : ''}</dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.quote.pdfArtifact">Pdf Artifact</Translate>
          </dt>
          <dd>{quoteEntity.pdfArtifact ? quoteEntity.pdfArtifact.fileName : ''}</dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.quote.session">Session</Translate>
          </dt>
          <dd>{quoteEntity.session ? quoteEntity.session.sessionCode : ''}</dd>
        </dl>
        <Button as={Link as any} to="/quote" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/quote/${quoteEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default QuoteDetail;
